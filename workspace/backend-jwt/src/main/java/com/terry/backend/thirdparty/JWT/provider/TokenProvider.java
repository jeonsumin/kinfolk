package com.terry.backend.thirdparty.JWT.provider;

import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.thirdparty.JWT.DTO.TokenDTO;
import com.terry.backend.thirdparty.JWT.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class TokenProvider {
    private final Key key;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;
    private final TokenBlacklistService blacklistService;

    public TokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.token-validity}") long accessTokenExpireTime,
            @Value("${jwt.refresh-token-validity}") long refreshTokenExpireTime,
            TokenBlacklistService blacklistService
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        this.blacklistService = blacklistService;
    }

    /**
     * Access Token 과 Refresh Token을 함께 생성하여 반환
     * @param user 인증된 사용자 정보
     * @return TokenDTO (accessToken, refreshToken, 각 만료일시 포함)
     */
    public TokenDTO generateTokenPair(UserDTO user) {
        ZonedDateTime now = ZonedDateTime.now();

        Date accessExpire  = Date.from(now.plusSeconds(accessTokenExpireTime).toInstant());
        Date refreshExpire = Date.from(now.plusSeconds(refreshTokenExpireTime).toInstant());

        String accessToken  = createToken(user, accessExpire);
        String refreshToken = createRefreshToken(user.getId(), refreshExpire);

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireTime(accessExpire)
                .refreshTokenExpireTime(refreshExpire)
                .build();
    }

    /**
     * Access Token 단독 생성 (기존 호환용)
     * @param user 인증된 사용자 정보
     * @return Access Token 문자열
     */
    public String generationToken(UserDTO user) {
        Date expire = Date.from(ZonedDateTime.now().plusSeconds(accessTokenExpireTime).toInstant());
        return createToken(user, expire);
    }

    /**
     * 사용자 정보 클레임을 담은 Access Token 생성
     */
    private String createToken(UserDTO user, Date expireDate) {
        String jti = UUID.randomUUID().toString();

        Claims claims = Jwts.claims();
        claims.put("id", user.getId());
        claims.put("username", user.getLoginId());
        claims.put("authority", user.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .setClaims(claims)
                .setId(jti)  // JWT ID 설정
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setIssuer("terry-backend-jwt")  // 발급자 설정
                .setAudience("terry-backend-clients")  // 대상 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * userId만 담은 Refresh Token 생성
     */
    private String createRefreshToken(String userId, Date expireDate) {
        String jti = UUID.randomUUID().toString();

        Claims claims = Jwts.claims();
        claims.put("id", userId);
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setId(jti)  // JWT ID 설정
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .setIssuer("terry-backend-jwt")
                .setAudience("terry-backend-clients")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token에서 login id 가져오기
     * @param token jwt Token
     * @return 로그인 id
     */
    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public String getUserId(String token) {
        return parseClaims(token).get("id", String.class);
    }

    /**
     * JWT 검증 (서명, 만료, 블랙리스트 확인)
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer("terry-backend-jwt")  // 발급자 검증
                .requireAudience("terry-backend-clients")  // 대상 검증
                .build()
                .parseClaimsJws(token)
                .getBody();

            // 블랙리스트 확인
            String jti = claims.getId();
            if (jti != null && blacklistService.isBlacklisted(jti)) {
                log.info("JWT Token is blacklisted: {}", maskToken(jti));
                return false;
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * 토큰을 무효화 (로그아웃, 보안 사고 등)
     */
    public void revokeToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            if (jti != null) {
                blacklistService.blacklistToken(jti);
                log.info("Token revoked: {}", maskToken(jti));
            }
        } catch (Exception e) {
            log.warn("Failed to revoke token: {}", e.getMessage());
        }
    }

    /**
     * 사용자의 모든 토큰을 무효화
     */
    public void revokeAllUserTokens(String userId) {
        blacklistService.blacklistAllUserTokens(userId);
    }

    /**
     * 유효한 토큰에서만 클레임을 파싱한다.
     * 만료된 토큰에서 클레임을 꺼내는 것은 보안상 위험하므로 예외를 그대로 전파한다.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰을 마스킹해서 로그에 안전하게 출력
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "***" + token.substring(token.length() - 4);
    }
}
