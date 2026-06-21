package com.terry.backend.web.security.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RateLimitService 단위 테스트
 * - 로그인 및 토큰 갱신에 대한 Rate Limiting 기능 검증
 * - IP 기반 시간 윈도우 제한 테스트
 */
class RateLimitServiceTest {

    private RateLimitService rateLimitService;
    private static final String TEST_IP = "192.168.1.100";
    private static final String ANOTHER_IP = "192.168.1.101";

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
    }

    // -----------------------------------------------------------------------
    // 로그인 Rate Limiting 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그인: 제한 횟수 이내에서는 허용되어야 한다")
    void loginRateLimit_withinLimit_shouldAllow() {
        // given: 최대 5회까지 허용
        for (int i = 1; i <= 5; i++) {
            // when
            boolean allowed = rateLimitService.isLoginAllowed(TEST_IP);

            // then
            assertThat(allowed).isTrue();

            // 실패한 로그인으로 기록
            rateLimitService.recordLoginAttempt(TEST_IP, false);
        }
    }

    @Test
    @DisplayName("로그인: 제한 횟수 초과 시 차단되어야 한다")
    void loginRateLimit_exceedsLimit_shouldBlock() {
        // given: 5회 실패 후
        for (int i = 1; i <= 5; i++) {
            rateLimitService.isLoginAllowed(TEST_IP);
            rateLimitService.recordLoginAttempt(TEST_IP, false);
        }

        // when: 6번째 시도
        boolean allowed = rateLimitService.isLoginAllowed(TEST_IP);

        // then: 차단되어야 함
        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("로그인: 성공한 로그인은 횟수에 포함하지만 제한에 영향을 주지 않는다")
    void loginRateLimit_successfulLogin_shouldNotBlock() {
        // given: 4회 실패 + 1회 성공
        for (int i = 1; i <= 4; i++) {
            rateLimitService.isLoginAllowed(TEST_IP);
            rateLimitService.recordLoginAttempt(TEST_IP, false);
        }

        // 성공한 로그인
        rateLimitService.isLoginAllowed(TEST_IP);
        rateLimitService.recordLoginAttempt(TEST_IP, true);

        // when: 추가 시도
        boolean allowed = rateLimitService.isLoginAllowed(TEST_IP);

        // then: 여전히 허용되어야 함
        assertThat(allowed).isTrue();
    }

    @Test
    @DisplayName("로그인: IP별로 독립적으로 제한되어야 한다")
    void loginRateLimit_differentIPs_shouldBeIndependent() {
        // given: TEST_IP로 5회 실패
        for (int i = 1; i <= 5; i++) {
            rateLimitService.isLoginAllowed(TEST_IP);
            rateLimitService.recordLoginAttempt(TEST_IP, false);
        }

        // when: ANOTHER_IP로 시도
        boolean allowed = rateLimitService.isLoginAllowed(ANOTHER_IP);

        // then: 다른 IP는 영향받지 않음
        assertThat(allowed).isTrue();
    }

    // -----------------------------------------------------------------------
    // 토큰 갱신 Rate Limiting 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("토큰 갱신: 제한 횟수 이내에서는 허용되어야 한다")
    void refreshRateLimit_withinLimit_shouldAllow() {
        // given: 최대 10회까지 허용
        for (int i = 1; i <= 10; i++) {
            // when
            boolean allowed = rateLimitService.isRefreshAllowed(TEST_IP);

            // then
            assertThat(allowed).isTrue();

            // 실패한 갱신으로 기록
            rateLimitService.recordRefreshAttempt(TEST_IP, false);
        }
    }

    @Test
    @DisplayName("토큰 갱신: 제한 횟수 초과 시 차단되어야 한다")
    void refreshRateLimit_exceedsLimit_shouldBlock() {
        // given: 10회 실패 후
        for (int i = 1; i <= 10; i++) {
            rateLimitService.isRefreshAllowed(TEST_IP);
            rateLimitService.recordRefreshAttempt(TEST_IP, false);
        }

        // when: 11번째 시도
        boolean allowed = rateLimitService.isRefreshAllowed(TEST_IP);

        // then: 차단되어야 함
        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("토큰 갱신: 로그인과 독립적으로 제한되어야 한다")
    void refreshRateLimit_independentFromLogin_shouldWork() {
        // given: 로그인을 5회 실패로 차단
        for (int i = 1; i <= 5; i++) {
            rateLimitService.isLoginAllowed(TEST_IP);
            rateLimitService.recordLoginAttempt(TEST_IP, false);
        }

        // when: 토큰 갱신 시도
        boolean loginAllowed = rateLimitService.isLoginAllowed(TEST_IP);
        boolean refreshAllowed = rateLimitService.isRefreshAllowed(TEST_IP);

        // then: 로그인은 차단, 토큰 갱신은 허용
        assertThat(loginAllowed).isFalse();
        assertThat(refreshAllowed).isTrue();
    }

    // -----------------------------------------------------------------------
    // 시간 윈도우 테스트 (실제로는 시간이 걸리므로 로직 테스트)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("동일 IP로 연속 요청 시 카운트가 누적되어야 한다")
    void rateLimit_sameIP_shouldAccumulate() {
        // given & when: 3번 연속 실패
        rateLimitService.isLoginAllowed(TEST_IP);
        rateLimitService.recordLoginAttempt(TEST_IP, false);

        rateLimitService.isLoginAllowed(TEST_IP);
        rateLimitService.recordLoginAttempt(TEST_IP, false);

        rateLimitService.isLoginAllowed(TEST_IP);
        rateLimitService.recordLoginAttempt(TEST_IP, false);

        // then: 아직 허용 (5회 미만)
        boolean allowed = rateLimitService.isLoginAllowed(TEST_IP);
        assertThat(allowed).isTrue();

        // 2번 더 실패
        rateLimitService.recordLoginAttempt(TEST_IP, false);
        rateLimitService.isLoginAllowed(TEST_IP);
        rateLimitService.recordLoginAttempt(TEST_IP, false);

        // then: 이제 차단
        allowed = rateLimitService.isLoginAllowed(TEST_IP);
        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("null IP 처리 시 예외가 발생하지 않아야 한다")
    void rateLimit_nullIP_shouldNotThrowException() {
        // when & then: 예외 발생하지 않음
        assertThat(rateLimitService.isLoginAllowed(null)).isTrue();
        assertThat(rateLimitService.isRefreshAllowed(null)).isTrue();

        // 기록도 예외 없이 처리
        rateLimitService.recordLoginAttempt(null, false);
        rateLimitService.recordRefreshAttempt(null, true);
    }

    @Test
    @DisplayName("빈 문자열 IP 처리 시 예외가 발생하지 않아야 한다")
    void rateLimit_emptyIP_shouldNotThrowException() {
        // when & then: 예외 발생하지 않음
        assertThat(rateLimitService.isLoginAllowed("")).isTrue();
        assertThat(rateLimitService.isRefreshAllowed("")).isTrue();

        rateLimitService.recordLoginAttempt("", false);
        rateLimitService.recordRefreshAttempt("", true);
    }
}