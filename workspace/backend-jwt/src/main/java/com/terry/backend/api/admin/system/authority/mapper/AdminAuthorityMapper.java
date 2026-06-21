package com.terry.backend.api.admin.system.authority.mapper;

import com.terry.backend.api.admin.system.authority.dto.AuthorityDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMemberDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthorityMenuDTO;
import com.terry.backend.api.admin.system.authority.dto.AuthoritySearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminAuthorityMapper {

    /**
     * 권한 목록을 가져온다
     *
     * @param  param 검색 파라미터
     * @return       검색된 권한 목록
     */
    List<AuthorityDTO> select(AuthoritySearchParam param);

    /**
     * 권한ID의 권한 사용자를 불러온다
     *
     * @param  authorityId 권한ID
     * @return             권한 사용자 목록
     */
    List<AuthorityMemberDTO> selectAuthorityMembers(@Param(value = "authorityId") String authorityId);

    /**
     * 권한ID의 권한 메뉴를 불러온다
     *
     * @param  authorityId 권한ID
     * @return             권한 메뉴 목록
     */
    List<AuthorityMenuDTO> selectAuthorityMenues(@Param(value = "authorityId") String authorityId);

    /**
     * 권한을 저장한다
     *
     * @param entity 저장할 권한 Object
     */
    void save(AuthorityDTO entity);

    /**
     * 권한을 삭제한다
     *
     * @param entity 삭제할 권한 Object
     */
    void delete(AuthorityDTO entity);

    /**
     * 권한 사용자를 삭제한다
     *
     * @param authorityId 권한ID *필수
     * @param memberId    사용자ID (null 허용, null인 경우, 전체 권한 사용자 삭제)
     */
    void deleteMembersById(@Param(value = "authorityId") String authorityId, @Param(value = "memberId") String memberId);

    /**
     * 권한 메뉴를 삭제한다
     *
     * @param authorityId 권한ID *필수
     * @param menuId      메뉴ID (null 허용 - null인 경우, 전체 권한 메뉴 삭제)
     */
    void deleteMenuesById(@Param(value = "authorityId") String authorityId, @Param(value = "menuId") String menuId);

    /**
     * 권한 사용자를 저장한다
     *
     * @param entity 저장될 권한 사용자 Object
     */
    void saveMember(AuthorityMemberDTO entity);

    /**
     * 권한 메뉴를 저장한다
     *
     * @param entity 저장될 권한 메뉴 Object
     */
    void saveMenu(AuthorityMenuDTO entity);

}
