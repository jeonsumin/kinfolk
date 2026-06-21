package com.terry.backend.thirdparty.JWT.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TokenBlacklistService 단위 테스트
 * - JWT 토큰 블랙리스트 기능 검증
 * - 토큰 무효화 및 검증 로직 테스트
 */
class TokenBlacklistServiceTest {

    private TokenBlacklistService blacklistService;
    private static final String TEST_TOKEN_ID = "test-token-id-12345";
    private static final String ANOTHER_TOKEN_ID = "another-token-id-67890";
    private static final String USER_ID = "user-001";

    @BeforeEach
    void setUp() {
        blacklistService = new TokenBlacklistService();
    }

    // -----------------------------------------------------------------------
    // 토큰 블랙리스트 기본 기능 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("새로운 토큰은 블랙리스트에 없어야 한다")
    void isBlacklisted_newToken_shouldReturnFalse() {
        // when
        boolean isBlacklisted = blacklistService.isBlacklisted(TEST_TOKEN_ID);

        // then
        assertThat(isBlacklisted).isFalse();
    }

    @Test
    @DisplayName("블랙리스트에 추가된 토큰은 무효화되어야 한다")
    void blacklistToken_addedToken_shouldBeBlacklisted() {
        // when
        blacklistService.blacklistToken(TEST_TOKEN_ID);

        // then
        assertThat(blacklistService.isBlacklisted(TEST_TOKEN_ID)).isTrue();
    }

    @Test
    @DisplayName("여러 토큰을 개별적으로 블랙리스트에 추가할 수 있어야 한다")
    void blacklistToken_multipleTokens_shouldWorkIndependently() {
        // when
        blacklistService.blacklistToken(TEST_TOKEN_ID);
        blacklistService.blacklistToken(ANOTHER_TOKEN_ID);

        // then
        assertThat(blacklistService.isBlacklisted(TEST_TOKEN_ID)).isTrue();
        assertThat(blacklistService.isBlacklisted(ANOTHER_TOKEN_ID)).isTrue();

        // 다른 토큰은 여전히 유효
        assertThat(blacklistService.isBlacklisted("other-token")).isFalse();
    }

    @Test
    @DisplayName("동일한 토큰을 중복으로 추가해도 문제없어야 한다")
    void blacklistToken_duplicateToken_shouldNotCauseIssue() {
        // when
        blacklistService.blacklistToken(TEST_TOKEN_ID);
        blacklistService.blacklistToken(TEST_TOKEN_ID); // 중복 추가

        // then
        assertThat(blacklistService.isBlacklisted(TEST_TOKEN_ID)).isTrue();
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(1);
    }

    // -----------------------------------------------------------------------
    // 사용자별 토큰 무효화 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("사용자 토큰 전체 무효화가 예외없이 실행되어야 한다")
    void blacklistAllUserTokens_shouldNotThrowException() {
        // when & then: 예외 발생하지 않음
        blacklistService.blacklistAllUserTokens(USER_ID);
        blacklistService.blacklistAllUserTokens(null);
        blacklistService.blacklistAllUserTokens("");
    }

    // -----------------------------------------------------------------------
    // 블랙리스트 크기 관리 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("블랙리스트 크기가 정확히 반환되어야 한다")
    void getBlacklistSize_shouldReturnCorrectSize() {
        // given
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(0);

        // when
        blacklistService.blacklistToken(TEST_TOKEN_ID);
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(1);

        blacklistService.blacklistToken(ANOTHER_TOKEN_ID);
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(2);

        // 동일 토큰 중복 추가 시에도 크기는 그대로
        blacklistService.blacklistToken(TEST_TOKEN_ID);
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(2);
    }

    // -----------------------------------------------------------------------
    // 엣지 케이스 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("null 토큰 ID 처리 시 예외가 발생하지 않아야 한다")
    void blacklistToken_nullTokenId_shouldNotThrowException() {
        // when & then: 예외 발생하지 않음
        blacklistService.blacklistToken(null);
        assertThat(blacklistService.isBlacklisted(null)).isFalse(); // null은 블랙리스트에 추가되지 않음
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(0); // 크기도 변하지 않음
    }

    @Test
    @DisplayName("빈 문자열 토큰 ID 처리가 정상 동작해야 한다")
    void blacklistToken_emptyTokenId_shouldWork() {
        // when
        blacklistService.blacklistToken("");

        // then
        assertThat(blacklistService.isBlacklisted("")).isTrue();
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("매우 긴 토큰 ID도 처리할 수 있어야 한다")
    void blacklistToken_longTokenId_shouldWork() {
        // given
        StringBuilder longTokenBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longTokenBuilder.append("a");
        }
        String longTokenId = longTokenBuilder.toString();

        // when
        blacklistService.blacklistToken(longTokenId);

        // then
        assertThat(blacklistService.isBlacklisted(longTokenId)).isTrue();
    }

    @Test
    @DisplayName("특수 문자가 포함된 토큰 ID도 처리할 수 있어야 한다")
    void blacklistToken_specialCharacters_shouldWork() {
        // given
        String specialTokenId = "token@#$%^&*()_+-=[]{}|;:,.<>?";

        // when
        blacklistService.blacklistToken(specialTokenId);

        // then
        assertThat(blacklistService.isBlacklisted(specialTokenId)).isTrue();
    }

    // -----------------------------------------------------------------------
    // 동시성 기본 테스트
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("동시에 여러 토큰을 추가해도 정상 동작해야 한다")
    void blacklistToken_concurrent_shouldWorkCorrectly() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        int tokensPerThread = 10;
        Thread[] threads = new Thread[numberOfThreads];

        // when: 여러 스레드에서 동시에 토큰 추가
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < tokensPerThread; j++) {
                    String tokenId = "thread-" + threadId + "-token-" + j;
                    blacklistService.blacklistToken(tokenId);
                }
            });
            threads[i].start();
        }

        // 모든 스레드 완료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // then: 모든 토큰이 정상 추가되었는지 확인
        assertThat(blacklistService.getBlacklistSize()).isEqualTo(numberOfThreads * tokensPerThread);

        // 각 토큰이 블랙리스트에 있는지 확인
        for (int i = 0; i < numberOfThreads; i++) {
            for (int j = 0; j < tokensPerThread; j++) {
                String tokenId = "thread-" + i + "-token-" + j;
                assertThat(blacklistService.isBlacklisted(tokenId)).isTrue();
            }
        }
    }
}