package com.terry.backend.api.admin.system.member.mapper;

import com.terry.backend.api.admin.system.member.dto.MemberAuthorityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberAuthorityMapper {

  List<MemberAuthorityDTO> selectAuthority(@Param(value = "userId") String userId);

  void delete(MemberAuthorityDTO dto);

  void deleteByUserIdAndAuthorityIds(@Param(value = "userId") String userId,
      @Param(value = "authorityIds") String authorityIds);

  void save(MemberAuthorityDTO dto);

  void saveUserAuthority(@Param(value = "userId") String userId, @Param(value = "authorityId") String authorityId);

}
