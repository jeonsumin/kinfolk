package com.terry.backend.web.setup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface SetupMapper {

    /**
     * 관리자 권한을 가진 사용자 수 조회
     */
    int countAdminUsers();

    /**
     * 로그인 아이디 중복 확인
     */
    boolean existsByLoginId(@Param("loginId") String loginId);

    /**
     * 사용자 생성
     */
    void insertUser(
            @Param("userId") String userId,
            @Param("loginId") String loginId,
            @Param("userName") String userName,
            @Param("password") String password,
            @Param("email") String email,
            @Param("userUse") String userUse,
            @Param("userLock") String userLock,
            @Param("registDt") LocalDateTime registDt
    );

    /**
     * 사용자 권한 부여
     */
    void insertUserAuthority(
            @Param("userId") String userId,
            @Param("authorId") String authorId
    );
}