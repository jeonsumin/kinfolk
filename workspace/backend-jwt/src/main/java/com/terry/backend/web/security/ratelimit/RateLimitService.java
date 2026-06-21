package com.terry.backend.web.security.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;

/**
 * 간단한 Rate Limiting 서비스
 * 프로덕션에서는 Redis나 다른 분산 캐시 사용 권장
 */
@Slf4j
@Service
public class RateLimitService {

    private final Map<String, AttemptInfo> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, AttemptInfo> refreshAttempts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 로그인: 5분 동안 5회, 토큰 갱신: 1분 동안 10회
    private static final int LOGIN_MAX_ATTEMPTS = 5;
    private static final int LOGIN_WINDOW_MINUTES = 5;
    private static final int REFRESH_MAX_ATTEMPTS = 10;
    private static final int REFRESH_WINDOW_MINUTES = 1;

    public RateLimitService() {
        // 5분마다 오래된 기록들을 정리
        scheduler.scheduleAtFixedRate(this::cleanupOldAttempts, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 로그인 시도 제한 확인
     * @param clientIP 클라이언트 IP
     * @return true면 허용, false면 제한
     */
    public boolean isLoginAllowed(String clientIP) {
        return isAllowed(clientIP, loginAttempts, LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW_MINUTES, "LOGIN");
    }

    /**
     * 토큰 갱신 시도 제한 확인
     * @param clientIP 클라이언트 IP
     * @return true면 허용, false면 제한
     */
    public boolean isRefreshAllowed(String clientIP) {
        return isAllowed(clientIP, refreshAttempts, REFRESH_MAX_ATTEMPTS, REFRESH_WINDOW_MINUTES, "REFRESH");
    }

    /**
     * 로그인 시도 기록
     * @param clientIP 클라이언트 IP
     * @param success 성공 여부
     */
    public void recordLoginAttempt(String clientIP, boolean success) {
        recordAttempt(clientIP, loginAttempts, success, "LOGIN");
    }

    /**
     * 토큰 갱신 시도 기록
     * @param clientIP 클라이언트 IP
     * @param success 성공 여부
     */
    public void recordRefreshAttempt(String clientIP, boolean success) {
        recordAttempt(clientIP, refreshAttempts, success, "REFRESH");
    }

    private boolean isAllowed(String key, Map<String, AttemptInfo> attempts,
                             int maxAttempts, int windowMinutes, String type) {
        // null이나 빈 key 처리
        if (key == null || key.trim().isEmpty()) {
            return true;
        }

        AttemptInfo info = attempts.get(key);
        LocalDateTime now = LocalDateTime.now();

        if (info == null) {
            return true;
        }

        // 시간 윈도우 초과 시 초기화
        if (ChronoUnit.MINUTES.between(info.getFirstAttempt(), now) >= windowMinutes) {
            attempts.remove(key);
            return true;
        }

        // 성공한 시도가 있으면 제한하지 않음
        if (info.hasSuccess()) {
            return true;
        }

        boolean allowed = info.getAttemptCount() < maxAttempts;
        if (!allowed) {
            log.warn("Rate limit exceeded for {} - IP: {}, attempts: {}/{} in {} minutes",
                type, key, info.getAttemptCount(), maxAttempts, windowMinutes);
        }

        return allowed;
    }

    private void recordAttempt(String key, Map<String, AttemptInfo> attempts,
                              boolean success, String type) {
        // null이나 빈 key 처리
        if (key == null || key.trim().isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        AttemptInfo info = attempts.get(key);

        if (info == null) {
            info = new AttemptInfo(now, 1, success);
        } else {
            // 시간 윈도우 체크
            int windowMinutes = type.equals("LOGIN") ? LOGIN_WINDOW_MINUTES : REFRESH_WINDOW_MINUTES;

            if (ChronoUnit.MINUTES.between(info.getFirstAttempt(), now) >= windowMinutes) {
                // 윈도우 초과 시 새로 시작
                info = new AttemptInfo(now, 1, success);
            } else {
                // 기존 윈도우 내에서 카운트 증가
                info = new AttemptInfo(info.getFirstAttempt(), info.getAttemptCount() + 1,
                                     info.hasSuccess() || success);
            }
        }

        attempts.put(key, info);

        log.debug("{} attempt recorded - IP: {}, count: {}, success: {}",
            type, key, info.getAttemptCount(), success);
    }

    private void cleanupOldAttempts() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(Math.max(LOGIN_WINDOW_MINUTES, REFRESH_WINDOW_MINUTES));

        int removedLogin = cleanupMap(loginAttempts, cutoff);
        int removedRefresh = cleanupMap(refreshAttempts, cutoff);

        if (removedLogin > 0 || removedRefresh > 0) {
            log.debug("Rate limit cleanup - removed {} login attempts, {} refresh attempts",
                removedLogin, removedRefresh);
        }
    }

    private int cleanupMap(Map<String, AttemptInfo> map, LocalDateTime cutoff) {
        int removedCount = 0;
        var iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().getFirstAttempt().isBefore(cutoff)) {
                iterator.remove();
                removedCount++;
            }
        }
        return removedCount;
    }

    // 내부 클래스
    private static class AttemptInfo {
        private final LocalDateTime firstAttempt;
        private final int attemptCount;
        private final boolean hasSuccess;

        public AttemptInfo(LocalDateTime firstAttempt, int attemptCount, boolean hasSuccess) {
            this.firstAttempt = firstAttempt;
            this.attemptCount = attemptCount;
            this.hasSuccess = hasSuccess;
        }

        public LocalDateTime getFirstAttempt() { return firstAttempt; }
        public int getAttemptCount() { return attemptCount; }
        public boolean hasSuccess() { return hasSuccess; }
    }
}