package com.terry.backend.web.security.service;

import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.messages.handler.ResponseMessages;
import com.terry.backend.core.messages.util.MessageSourceUtils;
import com.terry.backend.core.security.dto.AuthorityDTO;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.thirdparty.JWT.DTO.TokenDTO;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.web.security.LoginRequest;
import com.terry.backend.web.security.MenuAuthority;
import com.terry.backend.web.security.RoleType;
import com.terry.backend.web.security.dto.RefreshTokenDTO;
import com.terry.backend.web.security.mapper.SecurityMapper;
import com.terry.backend.web.security.ratelimit.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SecurityService {

    private final SecurityMapper mapper;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RateLimitService rateLimitService;
    private final boolean isDebug;

    public SecurityService(
            SecurityMapper mapper
            , TokenProvider tokenProvider
            , PasswordEncoder passwordEncoder
            , RateLimitService rateLimitService
            , @Value("${debug}") boolean isDebug
    ) {
        this.mapper = mapper;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.rateLimitService = rateLimitService;
        this.isDebug = isDebug;
    }

    public MenuAuthority selectUserMenuAuthority(final String menuCode) throws Exception {
        String userId = SessionUtils.getUserId();

        // 시스템 관리자 메뉴
        if (menuCode.matches("^ADMIN.*$")) {
            List<AuthorityDTO> authorities = mapper.selectUserAuthorities(userId);
            List<RoleType> roleTypes = authorities
                    .stream()
                    .map(x -> RoleType.valueOf(x.getAuthorityCode()))
                    .filter(x -> x.isAdmin())
                    .collect(Collectors.toList());
            if (!roleTypes.isEmpty()) {
                log.debug("Admin menu access granted for user: {}, menu: {}", userId, menuCode);
                return MenuAuthority
                        .builder()
                        .authorityValue(7)
                        .build();
            } else {
                log.warn("Unauthorized admin menu access attempt - User: {}, Menu: {}", userId, menuCode);
            }
        }
        // 게시판 레벨 권한 체크 (정규식 수정)
        else if (menuCode.matches("^BBS.*")) {
            return MenuAuthority
                    .builder()
                    .authorityValue(7)
                    .build();
        }
        // 나머지 메뉴
        else {
            MenuAuthority authority = mapper.selectUserMenuAuthority(menuCode, SessionUtils.getUsername());
            if (authority != null && authority.isRead()) {
                return authority;
            }
        }
        throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
    }

    /**
     * API LOGIN
     * Access Token + Refresh Token을 함께 발급하고 Refresh Token을 DB에 저장한다.
     *
     * @param loginRequest 로그인 정보
     * @return AccessToken, RefreshToken
     */
    @Transactional
    public ResponseMessages login(LoginRequest loginRequest) throws Exception {
        // 입력 데이터 검증
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            return ResponseMessages.fail("아이디를 입력해주세요.");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return ResponseMessages.fail("비밀번호를 입력해주세요.");
        }

        String clientIP = getClientIP();
        String username = loginRequest.getUsername();

        // Rate Limiting 확인
        if (!rateLimitService.isLoginAllowed(clientIP)) {
            log.warn("Login rate limit exceeded - IP: {}", clientIP);
            return ResponseMessages.fail("너무 많은 로그인 시도가 있었습니다. 잠시 후 다시 시도해주세요.");
        }

        try {
            UserDTO user = mapper.selectByUsername(username);
            boolean userExists = (user != null);
            boolean passwordMatches = false;

            if (userExists) {
                passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            } else {
                // 타이밍 공격 방지를 위한 더미 비밀번호 검증
                passwordEncoder.matches("dummy-password", "$2a$10$dummyHashForTimingAttackPrevention");
            }

            if (!userExists || !passwordMatches) {
                rateLimitService.recordLoginAttempt(clientIP, false);
                log.debug("Login failed - Username: {}, IP: {}, Reason: {}",
                    username, clientIP, userExists ? "wrong_password" : "user_not_found");
                return ResponseMessages.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
            }

            // 사용자 계정 상태 확인
            if (Boolean.FALSE.equals(user.getUse())) {
                log.warn("Login failed - disabled account - Username: {}, IP: {}", username, clientIP);
                return ResponseMessages.fail("사용할 수 없는 계정입니다.");
            }

            if (Boolean.TRUE.equals(user.getLock())) {
                log.warn("Login failed - locked account - Username: {}, IP: {}", username, clientIP);
                return ResponseMessages.fail("잠긴 계정입니다. 관리자에게 문의하세요.");
            }

            user.setAuthorities(mapper.selectUserAuthorities(user.getId()));
            mapper.updateLastLogin(user.getId());

            Map<String, String> data = upsertRefreshToken(user);

            rateLimitService.recordLoginAttempt(clientIP, true);
            log.info("Login successful - Username: {}, IP: {}", username, clientIP);
            return ResponseMessages.success(data);

        } catch (Exception e) {
            rateLimitService.recordLoginAttempt(clientIP, false);
            log.error("Login error - Username: {}, IP: {}, Error: {}", username, clientIP, e.getMessage());
            return ResponseMessages.fail("로그인 중 오류가 발생했습니다.");
        }
    }

    /**
     * Refresh Token으로 Access Token을 재발급한다.
     * 유효한 Refresh Token이면 새 Access Token + Refresh Token 쌍을 반환하고 DB를 갱신한다.
     *
     * @param refreshToken 클라이언트가 전달한 Refresh Token
     * @return 새 AccessToken, RefreshToken
     */
    @Transactional
    public ResponseMessages reissue(String refreshToken) throws Exception {
        // Refresh Token 기본 검증
        if (refreshToken.trim().isEmpty()) {
            return ResponseMessages.fail("유효하지 않은 토큰입니다.");
        }

        String clientIP = getClientIP();

        // Rate Limiting 확인
        if (!rateLimitService.isRefreshAllowed(clientIP)) {
            log.warn("Refresh rate limit exceeded - IP: {}", clientIP);
            return ResponseMessages.fail("너무 많은 요청이 있었습니다. 잠시 후 다시 시도해주세요.");
        }

        try {
            if (!tokenProvider.validateToken(refreshToken)) {
                rateLimitService.recordRefreshAttempt(clientIP, false);
                log.warn("Invalid refresh token attempt from IP: {}", clientIP);
                return ResponseMessages.fail("토큰이 유효하지 않습니다.");
            }

            String userId = tokenProvider.getUserId(refreshToken);
            String hashedToken = hashRefreshToken(refreshToken);

            Optional<RefreshTokenDTO> storedTokenOpt = mapper.findRefreshTokenByUserId(userId);
            if (storedTokenOpt.isEmpty()) {
                rateLimitService.recordRefreshAttempt(clientIP, false);
                log.warn("Refresh token not found for user: {}, IP: {}", userId, clientIP);
                return ResponseMessages.fail("토큰이 유효하지 않습니다.");
            }

            RefreshTokenDTO storedToken = storedTokenOpt.get();

            // 해시 비교로 변경 (만약 DB에 해시로 저장되어 있다면)
            if (!storedToken.getRefreshToken().equals(hashedToken)) {
                rateLimitService.recordRefreshAttempt(clientIP, false);
                log.warn("Refresh token mismatch for user: {}, IP: {}", userId, clientIP);
                // 토큰 탈취 의심 시 모든 토큰 무효화
                mapper.deleteRefreshTokenByUserId(userId);
                return ResponseMessages.fail("토큰이 유효하지 않습니다.");
            }

            Optional<UserDTO> userOpt = mapper.findByUserId(userId);
            if (userOpt.isEmpty()) {
                rateLimitService.recordRefreshAttempt(clientIP, false);
                log.warn("User not found during token refresh: {}, IP: {}", userId, clientIP);
                return ResponseMessages.fail("토큰이 유효하지 않습니다.");
            }

            UserDTO user = userOpt.get();

            // 사용자 계정 상태 재확인
            if (Boolean.FALSE.equals(user.getUse()) || Boolean.TRUE.equals(user.getLock())) {
                rateLimitService.recordRefreshAttempt(clientIP, false);
                log.warn("Token refresh denied - account disabled/locked - User: {}, IP: {}", userId, clientIP);
                mapper.deleteRefreshTokenByUserId(userId);
                return ResponseMessages.fail("계정을 사용할 수 없습니다.");
            }

            user.setAuthorities(mapper.selectUserAuthorities(user.getId()));
            Map<String, String> data = upsertRefreshToken(user);

            rateLimitService.recordRefreshAttempt(clientIP, true);
            log.info("Token refresh successful for user: {}, IP: {}", userId, clientIP);
            return ResponseMessages.success(data);

        } catch (Exception e) {
            rateLimitService.recordRefreshAttempt(clientIP, false);
            log.error("Token refresh error from IP: {}, Error: {}", clientIP, e.getMessage());
            return ResponseMessages.fail("토큰 재발급 중 오류가 발생했습니다.");
        }
    }

    private Map<String, String> upsertRefreshToken(UserDTO user) {

        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        // Refresh Token 갱신 (해시로 저장)
        mapper.upsertRefreshToken(RefreshTokenDTO.builder()
                .userId(user.getId())
                .refreshToken(hashRefreshToken(tokenDTO.getRefreshToken()))
                .expireDt(tokenDTO.getRefreshTokenExpireTime())
                .build());

        Map<String, String> data = new LinkedHashMap<>();
        data.put("accessToken", tokenDTO.getAccessToken());
        data.put("refreshToken", tokenDTO.getRefreshToken());
        return data;
    }
    /**
     * 로그아웃 시 DB의 Refresh Token을 삭제하고 토큰을 블랙리스트에 추가한다.
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void revokeRefreshToken(String userId) {
        String clientIP = getClientIP();

        try {
            // DB에서 저장된 Refresh Token 조회
            Optional<RefreshTokenDTO> storedToken = mapper.findRefreshTokenByUserId(userId);
            if (storedToken.isPresent()) {
                // 토큰 블랙리스트에 추가 (복호화 필요 시)
                tokenProvider.revokeAllUserTokens(userId);
            }

            // DB에서 Refresh Token 삭제
            mapper.deleteRefreshTokenByUserId(userId);

            log.info("User logout - tokens revoked for user: {}, IP: {}", userId, clientIP);
        } catch (Exception e) {
            log.error("Error revoking tokens for user: {}, IP: {}, Error: {}", userId, clientIP, e.getMessage());
        }
    }

    /**
     * 보안 사고나 계정 잠금 시 사용자의 모든 토큰을 무효화
     */
    @Transactional
    public void revokeAllUserTokens(String userId, String reason) {
        String clientIP = getClientIP();

        try {
            tokenProvider.revokeAllUserTokens(userId);
            mapper.deleteRefreshTokenByUserId(userId);

            log.warn("All tokens revoked for user: {}, IP: {}, Reason: {}", userId, clientIP, reason);
        } catch (Exception e) {
            log.error("Error revoking all tokens for user: {}, IP: {}, Error: {}", userId, clientIP, e.getMessage());
        }
    }

    private String getClientIP() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIP = request.getHeader("X-Real-IP");
            if (xRealIP != null && !xRealIP.isEmpty()) {
                return xRealIP;
            }

            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error hashing refresh token: {}", e.getMessage());
            return token; // fallback to plain comparison
        }
    }

}
