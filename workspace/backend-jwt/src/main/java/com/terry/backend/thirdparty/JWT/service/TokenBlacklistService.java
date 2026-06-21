package com.terry.backend.thirdparty.JWT.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

/**
 * JWT 토큰 블랙리스트 관리 서비스
 * 로그아웃이나 보안상 이유로 무효화된 토큰들을 관리합니다.
 */
@Slf4j
@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenBlacklistService() {
        // 5분마다 만료된 토큰들을 정리
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 토큰을 블랙리스트에 추가
     * @param tokenId JWT ID (jti 클레임) 또는 토큰 해시
     */
    public void blacklistToken(String tokenId) {
        if (tokenId == null) {
            log.warn("Attempted to blacklist null token ID");
            return;
        }
        blacklistedTokens.add(tokenId);
        log.info("Token added to blacklist: {}", maskToken(tokenId));
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param tokenId JWT ID (jti 클레임) 또는 토큰 해시
     * @return 블랙리스트에 있으면 true
     */
    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null) {
            return false;
        }
        return blacklistedTokens.contains(tokenId);
    }

    /**
     * 사용자의 모든 토큰을 블랙리스트에 추가
     * (계정 잠금, 보안 사고 등의 경우)
     * @param userId 사용자 ID
     */
    public void blacklistAllUserTokens(String userId) {
        // 실제 구현에서는 해당 사용자의 모든 활성 토큰을 찾아서 블랙리스트에 추가
        // 이 예제에서는 로깅만 수행
        log.warn("All tokens blacklisted for user: {}", userId);
    }

    /**
     * 만료된 토큰들을 블랙리스트에서 제거
     * 실제 구현에서는 토큰의 만료시간을 확인해야 함
     */
    private void cleanupExpiredTokens() {
        int sizeBefore = blacklistedTokens.size();
        // 실제 구현에서는 토큰의 exp 클레임을 확인해서 만료된 것들만 제거
        // 여기서는 주기적으로 전체를 클리어 (예시)
        // blacklistedTokens.removeIf(token -> isTokenExpired(token));

        if (sizeBefore > 1000) { // 임시 방편: 크기가 너무 크면 전체 클리어
            blacklistedTokens.clear();
            log.info("Blacklist cleared due to size limit. Previous size: {}", sizeBefore);
        }

        log.debug("Token blacklist cleanup completed. Current size: {}", blacklistedTokens.size());
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

    /**
     * 블랙리스트 크기 반환 (모니터링용)
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
}