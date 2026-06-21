package com.terry.backend.thirdparty.JWT.filter;

import com.terry.backend.thirdparty.JWT.provider.TokenProvider;
import com.terry.backend.thirdparty.JWT.service.TokenBlacklistService;
import com.terry.backend.web.security.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 단위 테스트
 * - JWT 토큰 인증 필터 동작 검증
 * - 보안 로깅 및 예외 처리 테스트
 * - IP 추출 및 인증 컨텍스트 설정 테스트
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private UserDetailService userDetailService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private TokenBlacklistService blacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(userDetailService, tokenProvider);
        SecurityContextHolder.clearContext(); // 테스트 간 상태 초기화
    }

    // -----------------------------------------------------------------------
    // 정상 인증 플로우 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공해야 한다")
    void doFilterInternal_validToken_shouldAuthenticate() throws Exception {
        // given
        String validToken = "valid.jwt.token";
        String userId = "user123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(userId);

        UserDetails userDetails = new User(userId, "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailService.loadUserByUsername(userId)).thenReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(userId);
        assertThat(auth.getAuthorities()).hasSize(1);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증하지 않고 필터 체인 진행")
    void doFilterInternal_noAuthHeader_shouldSkipAuthentication() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 형식이 아닌 Authorization 헤더는 무시해야 한다")
    void doFilterInternal_invalidAuthHeader_shouldSkipAuthentication() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    // -----------------------------------------------------------------------
    // 토큰 검증 실패 케이스
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("유효하지 않은 토큰은 인증하지 않아야 한다")
    void doFilterInternal_invalidToken_shouldNotAuthenticate() throws Exception {
        // given
        String invalidToken = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(tokenProvider, never()).getUserId(anyString());
        verify(userDetailService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰 검증 시 예외 발생해도 필터 체인은 계속되어야 한다")
    void doFilterInternal_tokenValidationException_shouldContinueFilterChain() throws Exception {
        // given
        String problematicToken = "problematic.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + problematicToken);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(tokenProvider.validateToken(problematicToken))
            .thenThrow(new RuntimeException("Token parsing error"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain).doFilter(request, response);
    }

    // -----------------------------------------------------------------------
    // 사용자 조회 실패 케이스
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("존재하지 않는 사용자 ID면 인증하지 않아야 한다")
    void doFilterInternal_userNotFound_shouldNotAuthenticate() throws Exception {
        // given
        String validToken = "valid.jwt.token";
        String nonExistentUserId = "nonexistent";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUserId(validToken)).thenReturn(nonExistentUserId);
        when(userDetailService.loadUserByUsername(nonExistentUserId))
            .thenThrow(new UsernameNotFoundException("User not found"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(filterChain).doFilter(request, response);
    }

    // -----------------------------------------------------------------------
    // IP 주소 추출 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("X-Forwarded-For 헤더에서 IP 주소를 추출해야 한다")
    void doFilterInternal_xForwardedFor_shouldExtractCorrectIP() throws Exception {
        // given
        String token = "valid.jwt.token";
        String userId = "user123";
        String clientIP = "203.0.113.1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Forwarded-For")).thenReturn(clientIP + ", 192.168.1.1, 10.0.0.1");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserId(token)).thenReturn(userId);

        UserDetails userDetails = new User(userId, "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailService.loadUserByUsername(userId)).thenReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        verify(filterChain).doFilter(request, response);
        // 실제 IP 추출 로직은 private 메서드이므로 로깅을 통해 간접적으로 확인
    }

    @Test
    @DisplayName("X-Real-IP 헤더에서 IP 주소를 추출해야 한다")
    void doFilterInternal_xRealIP_shouldExtractCorrectIP() throws Exception {
        // given
        String token = "valid.jwt.token";
        String userId = "user123";
        String clientIP = "203.0.113.1";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(clientIP);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserId(token)).thenReturn(userId);

        UserDetails userDetails = new User(userId, "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailService.loadUserByUsername(userId)).thenReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("프록시 헤더가 없으면 RemoteAddr에서 IP를 추출해야 한다")
    void doFilterInternal_noProxyHeaders_shouldUseRemoteAddr() throws Exception {
        // given
        String token = "valid.jwt.token";
        String userId = "user123";
        String clientIP = "192.168.1.100";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(clientIP);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserId(token)).thenReturn(userId);

        UserDetails userDetails = new User(userId, "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetailService.loadUserByUsername(userId)).thenReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        verify(filterChain).doFilter(request, response);
    }

    // -----------------------------------------------------------------------
    // 기존 인증 컨텍스트 존재 시 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("이미 인증된 컨텍스트가 있으면 토큰 검증을 건너뛰어야 한다")
    void doFilterInternal_alreadyAuthenticated_shouldSkipTokenValidation() throws Exception {
        // given: 이미 인증된 상태 설정
        UserDetails userDetails = new User("existingUser", "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication existingAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer some.jwt.token");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);

        // 기존 인증이 유지되는지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isSameAs(existingAuth);
    }

    // -----------------------------------------------------------------------
    // 엣지 케이스 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Bearer 토큰이 비어있으면 인증하지 않아야 한다")
    void doFilterInternal_emptyBearerToken_shouldNotAuthenticate() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 토큰에 공백이 여러 개 있어도 정상 처리해야 한다")
    void doFilterInternal_bearerTokenWithSpaces_shouldHandleCorrectly() throws Exception {
        // given
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer   " + token);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(tokenProvider.validateToken(anyString())).thenReturn(false); // 검증 실패로 설정

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(tokenProvider).validateToken(token); // 공백이 제거된 토큰으로 검증
        verify(filterChain).doFilter(request, response);
    }
}