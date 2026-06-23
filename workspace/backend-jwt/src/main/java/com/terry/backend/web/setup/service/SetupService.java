package com.terry.backend.web.setup.service;

import com.terry.backend.web.setup.dto.AdminSetupRequest;
import com.terry.backend.web.setup.mapper.SetupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetupService {

    private final SetupMapper setupMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 시스템에 관리자 계정이 존재하는지 확인
     */
    public boolean checkAdminExists() {
        try {
            int adminCount = setupMapper.countAdminUsers();
            log.info("Admin count: {}", adminCount);
            return adminCount > 0;
        } catch (Exception e) {
            log.error("Error checking admin existence", e);
            return false;
        }
    }

    /**
     * 첫 번째 관리자 계정 생성
     */
    @Transactional
    public void createFirstAdmin(AdminSetupRequest request) {
        // 이미 관리자가 존재하는지 확인
        if (checkAdminExists()) {
            throw new IllegalStateException("이미 관리자 계정이 존재합니다.");
        }

        // 중복된 로그인 아이디 확인
        if (setupMapper.existsByLoginId(request.getLoginId())) {
            throw new IllegalStateException("이미 존재하는 로그인 아이디입니다.");
        }

        try {
            String userId = "admin-" + UUID.randomUUID().toString();
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            //초기 권한 설정
            String authoAdminId = "author-" + UUID.randomUUID().toString();
            String authoUserId = "author-" + UUID.randomUUID().toString();
            setupMapper.insertCurrentAuthority(authoAdminId,"ADMIN","관리자",userId);
            setupMapper.insertCurrentAuthority(authoUserId,"USER","사용자",userId);

            // 사용자 생성
            setupMapper.insertUser(
                userId,
                request.getLoginId(),
                request.getUserName(),
                hashedPassword,
                request.getEmail(),
                "Y", // 사용 여부
                "N", // 잠금 여부
                LocalDateTime.now()
            );

            // 관리자 권한 부여
            setupMapper.insertUserAuthority(userId, authoAdminId); // ROLE_ADMIN

            log.info("First admin account created successfully: {}", request.getLoginId());

        } catch (Exception e) {
            log.error("Error creating first admin account", e);
            throw new RuntimeException("관리자 계정 생성 중 오류가 발생했습니다.", e);
        }
    }
}
