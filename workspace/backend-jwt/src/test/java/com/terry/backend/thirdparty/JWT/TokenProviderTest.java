package com.terry.backend.thirdparty.JWT;

import com.terry.backend.core.security.dto.AuthorityDTO;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.thirdparty.JWT.DTO.TokenDTO;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.thirdparty.JWT.service.TokenBlacklistService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TokenProvider 단위 테스트
 * - Spring Context 없이 TokenProvider를 직접 생성하여 테스트
 * - JWT_SECRET은 테스트 전용 값을 사용
 */
@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    // HS256에 충분한 길이(256bit 이상)의 Base64 인코딩 키
    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS1vbmx5LTEyMzQ1Njc4OTAxMjM0NTY=";

    @Mock
    private TokenBlacklistService mockBlacklistService;

    // access: 1800초, refresh: 604800초
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider(TEST_SECRET, 1800L, 604800L, mockBlacklistService);
    }

    private UserDTO buildTestUser() {
        AuthorityDTO authority = AuthorityDTO.builder()
                .authorityCode("ROLE_USER")
                .authority("ROLE_USER")
                .build();

        return UserDTO.builder()
                .id("user-001")
                .loginId("testuser")
                .password("encoded-password")
                .name("테스트 사용자")
                .use(true)
                .lock(false)
                .authorities(List.of(authority))
                .build();
    }

    // -----------------------------------------------------------------------
    // generateTokenPair() 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateTokenPair: Access Token과 Refresh Token이 모두 생성되어야 한다")
    void generateTokenPair_shouldReturnBothTokens() {
        UserDTO user = buildTestUser();

        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        assertThat(tokenDTO).isNotNull();
        assertThat(tokenDTO.getAccessToken()).isNotBlank();
        assertThat(tokenDTO.getRefreshToken()).isNotBlank();
        assertThat(tokenDTO.getAccessTokenExpireTime()).isNotNull();
        assertThat(tokenDTO.getRefreshTokenExpireTime()).isNotNull();
    }

    @Test
    @DisplayName("generateTokenPair: Access Token 만료 시간이 Refresh Token 만료 시간보다 빨라야 한다")
    void generateTokenPair_accessTokenExpiresSoonerThanRefreshToken() {
        UserDTO user = buildTestUser();

        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        assertThat(tokenDTO.getAccessTokenExpireTime())
                .isBefore(tokenDTO.getRefreshTokenExpireTime());
    }

    @Test
    @DisplayName("generateTokenPair: 생성된 Access Token과 Refresh Token은 서로 달라야 한다")
    void generateTokenPair_accessAndRefreshTokenShouldBeDifferent() {
        UserDTO user = buildTestUser();

        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        assertThat(tokenDTO.getAccessToken())
                .isNotEqualTo(tokenDTO.getRefreshToken());
    }

    // -----------------------------------------------------------------------
    // validateToken() 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("validateToken: 유효한 토큰은 true를 반환해야 한다")
    void validateToken_validToken_returnsTrue() {
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        assertThat(tokenProvider.validateToken(tokenDTO.getAccessToken())).isTrue();
        assertThat(tokenProvider.validateToken(tokenDTO.getRefreshToken())).isTrue();
    }

    @Test
    @DisplayName("validateToken: 만료된 토큰은 false를 반환해야 한다")
    void validateToken_expiredToken_returnsFalse() {
        // 만료 시간을 0초로 설정한 별도 TokenProvider 생성
        TokenProvider shortLivedProvider = new TokenProvider(TEST_SECRET, 0L, 0L, mockBlacklistService);
        UserDTO user = buildTestUser();

        TokenDTO tokenDTO = shortLivedProvider.generateTokenPair(user);

        // 만료된 토큰 검증
        assertThat(tokenProvider.validateToken(tokenDTO.getAccessToken())).isFalse();
        assertThat(tokenProvider.validateToken(tokenDTO.getRefreshToken())).isFalse();
    }

    @Test
    @DisplayName("validateToken: 변조된 토큰은 false를 반환해야 한다")
    void validateToken_tamperedToken_returnsFalse() {
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        // 토큰 뒤에 임의 문자열을 추가하여 변조
        String tamperedToken = tokenDTO.getAccessToken() + "tampered";

        assertThat(tokenProvider.validateToken(tamperedToken)).isFalse();
    }

    @Test
    @DisplayName("validateToken: 다른 키로 서명된 토큰은 false를 반환해야 한다")
    void validateToken_differentKeyToken_returnsFalse() {
        // 다른 키로 서명된 토큰 직접 생성
        String otherSecret = "b3RoZXItc2VjcmV0LWtleS1mb3ItdGVzdGluZy1wdXJwb3NlLW9ubHktOTg3NjU0MzIxMA==";
        TokenProvider otherProvider = new TokenProvider(otherSecret, 1800L, 604800L, mockBlacklistService);
        UserDTO user = buildTestUser();

        TokenDTO tokenDTO = otherProvider.generateTokenPair(user);

        // 현재 tokenProvider로 검증하면 false여야 함
        assertThat(tokenProvider.validateToken(tokenDTO.getAccessToken())).isFalse();
    }

    @Test
    @DisplayName("validateToken: 빈 문자열은 false를 반환해야 한다")
    void validateToken_emptyString_returnsFalse() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("validateToken: 완전히 엉뚱한 문자열은 false를 반환해야 한다")
    void validateToken_randomString_returnsFalse() {
        assertThat(tokenProvider.validateToken("not.a.jwt.token")).isFalse();
    }

    // -----------------------------------------------------------------------
    // getUserId() 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getUserId: Refresh Token에서 userId를 올바르게 추출해야 한다")
    void getUserId_fromRefreshToken_returnsCorrectUserId() {
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        String extractedUserId = tokenProvider.getUserId(tokenDTO.getRefreshToken());

        assertThat(extractedUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("getUserId: Access Token에서도 userId를 추출할 수 있어야 한다")
    void getUserId_fromAccessToken_returnsCorrectUserId() {
        UserDTO user = buildTestUser();
        TokenDTO tokenDTO = tokenProvider.generateTokenPair(user);

        String extractedUserId = tokenProvider.getUserId(tokenDTO.getAccessToken());

        assertThat(extractedUserId).isEqualTo(user.getId());
    }
}
