package com.terry.backend.api.user.mapper;

import com.terry.backend.api.user.dto.UserProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 1. 사용자 ID로 프로필 조회
     * @param id 사용자 ID
     */
    UserProfileDTO selectProfileById(@Param("id") String id);

    /**
     * 2. 사용자 이름(displayName) 수정
     * @param id   사용자 ID
     * @param name 변경할 이름
     */
    void updateDisplayName(@Param("id") String id, @Param("name") String name);
}
