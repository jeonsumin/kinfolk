package com.terry.backend.web.security.mapper;

import com.terry.backend.core.security.dto.AuthorityDTO;
import com.terry.backend.core.security.dto.UserDTO;
import com.terry.backend.web.security.MenuAuthority;
import com.terry.backend.web.security.dto.RefreshTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SecurityMapper {

    Optional<UserDTO> findByUserId(@Param(value = "id") String id);

    UserDTO selectByUsername(@Param(value = "username") String username);

    List<AuthorityDTO> selectUserAuthorities(@Param(value = "username") String username);

    MenuAuthority selectUserMenuAuthority(@Param(value = "menuCode") String menuCode, @Param(value = "username") String username);

    void updateLastLogin(@Param(value = "username") String username);

    // Refresh Token
    void upsertRefreshToken(RefreshTokenDTO refreshTokenDTO);

    Optional<RefreshTokenDTO> findRefreshTokenByUserId(@Param(value = "userId") String userId);

    void deleteRefreshTokenByUserId(@Param(value = "userId") String userId);

}
