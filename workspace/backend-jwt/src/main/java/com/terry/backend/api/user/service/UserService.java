package com.terry.backend.api.user.service;

import com.terry.backend.api.user.dto.UserProfileDTO;
import com.terry.backend.api.user.mapper.UserMapper;
import com.terry.backend.core.security.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public UserProfileDTO getMyProfile() {
        String userId = SessionUtils.getUserId();
        UserProfileDTO profile = mapper.selectProfileById(userId);
        if (profile == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return profile;
    }

    @Transactional
    public UserProfileDTO updateDisplayName(String name) {
        String userId = SessionUtils.getUserId();
        mapper.updateDisplayName(userId, name);
        return getMyProfile();
    }
}
