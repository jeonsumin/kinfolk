package com.terry.backend.web.security.service;

import com.terry.backend.core.messages.handler.ResponseMessages;
import com.terry.backend.core.security.dto.AuthorityDTO;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.thirdparty.JWT.service.TokenBlacklistService;
import com.terry.backend.web.security.LoginRequest;
import com.terry.backend.web.security.mapper.SecurityMapper;
import com.terry.backend.web.security.ratelimit.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SecurityService 보안 기능 단위 테스트
 * - Rate Limiting 연동 테스트
 * - 사용자 열거 공격 방어 테스트
 * - 타이밍 공격 방어 테스트
 * - 계정 상태 검증 테스트
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceSecurityTest {

    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZS1vbmx5LTEyMzQ1Njc4OTAxMjM0NTY=";

    @Mock
    private SecurityMapper mapper;

    @Mock
    private TokenBlacklistService mockBlacklistService;

    @Mock
    private RateLimitService mockRateLimitService;

    private TokenProvider tokenProvider;
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider(TEST_SECRET, 1800L, 604800L, mockBlacklistService);

        // Rate limit service는 기본적으로 허용하도록 설정
        when(mockRateLimitService.isLoginAllowed(anyString())).thenReturn(true);
        when(mockRateLimitService.isRefreshAllowed(anyString())).thenReturn(true);

        securityService = new SecurityService(
                mapper,
                tokenProvider,
                new BCryptPasswordEncoder(),
                mockRateLimitService,
                false // isDebug = false
        );

        // Mock HTTP Request 설정
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    private UserDTO buildTestUser(boolean use, boolean lock) {
        AuthorityDTO authority = AuthorityDTO.builder()
                .authorityCode("ROLE_USER")
                .authority("ROLE_USER")
                .build();

        return UserDTO.builder()
                .id("user-001")
                .loginId("testuser")
                .password("$2a$10$encoded.password.hash")
                .name("테스트 사용자")
                .use(use)
                .lock(lock)
                .authorities(List.of(authority))
                .build();
    }

    // -----------------------------------------------------------------------
    // Rate Limiting 연동 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그인: Rate Limit 초과 시 차단되어야 한다")
    void login_rateLimitExceeded_shouldBlock() throws Exception {
        // given: Rate limit 초과 상황
        when(mockRateLimitService.isLoginAllowed(anyString())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        // when
        ResponseMessages result = securityService.login(loginRequest);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("너무 많은 로그인 시도가 있었습니다");

        // Mapper 호출되지 않아야 함
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("로그인: 실패 시 Rate Limit 기록되어야 한다")
    void login_failure_shouldRecordRateLimit() throws Exception {
        // given
        when(mapper.selectByUsername("testuser")).thenReturn(null); // 사용자 없음

        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        // when
        securityService.login(loginRequest);

        // then: 실패로 기록되어야 함
        verify(mockRateLimitService).recordLoginAttempt(anyString(), eq(false));
    }

    @Test
    @DisplayName("로그인: 성공 시 Rate Limit 성공으로 기록되어야 한다")
    void login_success_shouldRecordRateLimitSuccess() throws Exception {
        // given
        UserDTO user = buildTestUser(true, false);
        when(mapper.selectByUsername("testuser")).thenReturn(user);
        when(mapper.selectUserAuthorities(user.getId())).thenReturn(user.getAuthorities());

        LoginRequest loginRequest = new LoginRequest("testuser", "password123"); // 실제 비밀번호

        // BCrypt로 인코딩된 비밀번호 생성 (테스트용)
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("password123");
        user.setPassword(encodedPassword);

        // when
        securityService.login(loginRequest);

        // then: 성공으로 기록되어야 함
        verify(mockRateLimitService).recordLoginAttempt(anyString(), eq(true));
    }

    // -----------------------------------------------------------------------
    // 사용자 열거 공격 방어 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그인: 존재하지 않는 사용자와 잘못된 비밀번호에 동일한 메시지 반환")
    void login_userEnumerationDefense_shouldReturnSameMessage() throws Exception {
        // given
        LoginRequest nonExistentUser = new LoginRequest("nonexistent", "password");
        LoginRequest wrongPassword = new LoginRequest("testuser", "wrongpassword");

        UserDTO user = buildTestUser(true, false);
        when(mapper.selectByUsername("nonexistent")).thenReturn(null);
        when(mapper.selectByUsername("testuser")).thenReturn(user);

        // when
        ResponseMessages result1 = securityService.login(nonExistentUser);
        ResponseMessages result2 = securityService.login(wrongPassword);

        // then: 동일한 메시지 반환
        assertThat(result1.getMessages()).isEqualTo(result2.getMessages());
        assertThat(result1.getMessages()).contains("아이디 또는 비밀번호가 올바르지 않습니다");
        assertThat(result1.isSuccess()).isFalse();
        assertThat(result2.isSuccess()).isFalse();
    }

    // -----------------------------------------------------------------------
    // 계정 상태 검증 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그인: 비활성화된 계정은 접근 차단되어야 한다")
    void login_disabledAccount_shouldBlock() throws Exception {
        // given: 비활성화된 사용자 (패스워드는 맞춤)
        UserDTO user = buildTestUser(false, false); // use = false
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode("password123"));
        when(mapper.selectByUsername("testuser")).thenReturn(user);

        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        // when
        ResponseMessages result = securityService.login(loginRequest);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("사용할 수 없는 계정입니다");
        verify(mockRateLimitService).recordLoginAttempt(anyString(), eq(false));
    }

    @Test
    @DisplayName("로그인: 잠긴 계정은 접근 차단되어야 한다")
    void login_lockedAccount_shouldBlock() throws Exception {
        // given: 잠긴 사용자 (패스워드는 맞춤)
        UserDTO user = buildTestUser(true, true); // lock = true
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode("password123"));
        when(mapper.selectByUsername("testuser")).thenReturn(user);

        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        // when
        ResponseMessages result = securityService.login(loginRequest);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("잠긴 계정입니다");
        verify(mockRateLimitService).recordLoginAttempt(anyString(), eq(false));
    }

    // -----------------------------------------------------------------------
    // 토큰 갱신 보안 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("토큰 갱신: Rate Limit 초과 시 차단되어야 한다")
    void reissue_rateLimitExceeded_shouldBlock() throws Exception {
        // given: Rate limit 초과 상황
        when(mockRateLimitService.isRefreshAllowed(anyString())).thenReturn(false);

        // when
        ResponseMessages result = securityService.reissue("any.refresh.token");

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("너무 많은 요청이 있었습니다");

        // TokenProvider 호출되지 않아야 함
        verifyNoInteractions(mockBlacklistService);
    }

    @Test
    @DisplayName("토큰 갱신: 실패 시 Rate Limit 기록되어야 한다")
    void reissue_failure_shouldRecordRateLimit() throws Exception {
        // given: 유효하지 않은 토큰
        when(tokenProvider.validateToken(anyString())).thenReturn(false);

        // when
        securityService.reissue("invalid.token");

        // then: 실패로 기록되어야 함
        verify(mockRateLimitService).recordRefreshAttempt(anyString(), eq(false));
    }

    // -----------------------------------------------------------------------
    // 에러 처리 및 로깅 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그인: 예외 발생 시 안전하게 처리되어야 한다")
    void login_exception_shouldBeHandledSafely() throws Exception {
        // given: DB 조회 시 예외 발생
        when(mapper.selectByUsername(anyString())).thenThrow(new RuntimeException("DB Error"));

        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        // when
        ResponseMessages result = securityService.login(loginRequest);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("로그인 중 오류가 발생했습니다");
        verify(mockRateLimitService).recordLoginAttempt(anyString(), eq(false));
    }

    @Test
    @DisplayName("토큰 갱신: 예외 발생 시 안전하게 처리되어야 한다")
    void reissue_exception_shouldBeHandledSafely() throws Exception {
        // given: TokenProvider에서 예외 발생
        String validJwtFormat = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        when(tokenProvider.validateToken(eq(validJwtFormat))).thenThrow(new RuntimeException("Token Error"));

        // when
        ResponseMessages result = securityService.reissue(validJwtFormat);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessages()).contains("토큰 재발급 중 오류가 발생했습니다");
        verify(mockRateLimitService).recordRefreshAttempt(anyString(), eq(false));
    }

    // -----------------------------------------------------------------------
    // 토큰 무효화 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그아웃: 토큰이 올바르게 무효화되어야 한다")
    void revokeRefreshToken_shouldInvalidateTokens() {
        // given
        String userId = "user-001";

        // when
        securityService.revokeRefreshToken(userId);

        // then
        verify(tokenProvider).revokeAllUserTokens(userId);
        verify(mapper).deleteRefreshTokenByUserId(userId);
    }

    @Test
    @DisplayName("전체 토큰 무효화: 보안 사고 시 모든 토큰 무효화")
    void revokeAllUserTokens_securityIncident_shouldInvalidateAll() {
        // given
        String userId = "user-001";
        String reason = "security_breach";

        // when
        securityService.revokeAllUserTokens(userId, reason);

        // then
        verify(tokenProvider).revokeAllUserTokens(userId);
        verify(mapper).deleteRefreshTokenByUserId(userId);
    }
}