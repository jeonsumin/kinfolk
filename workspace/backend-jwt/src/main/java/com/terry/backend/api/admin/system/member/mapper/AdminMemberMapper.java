package com.terry.backend.api.admin.system.member.mapper;


import com.terry.backend.api.admin.system.member.dto.MemberDTO;
import com.terry.backend.api.admin.system.member.dto.MemberSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberMapper {

    List<MemberDTO> select(MemberSearchParam param);

    MemberDTO selectById(@Param(value = "id") String id);

    void insert(MemberDTO entity);

    void update(MemberDTO entity);

    void deleteAuthorityLink(MemberDTO entity);

    void delete(MemberDTO entity);

}
