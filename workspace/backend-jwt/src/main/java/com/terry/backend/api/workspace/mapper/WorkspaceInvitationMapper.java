package com.terry.backend.api.workspace.mapper;

import com.terry.backend.api.workspace.dto.InvitationStatus;
import com.terry.backend.api.workspace.dto.WorkspaceInvitationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkspaceInvitationMapper {

    /**
     * 1. 초대 생성
     * @param dto 초대 정보
     */
    void insertInvitation(WorkspaceInvitationDTO dto);

    /**
     * 2. 워크스페이스 초대 목록 조회
     * @param wsId 워크스페이스 ID
     */
    List<WorkspaceInvitationDTO> selectInvitationsByWsId(@Param("wsId") String wsId);

    /**
     * 3. 토큰으로 초대 단건 조회 (워크스페이스명 · 초대자명 JOIN)
     * @param token 초대 토큰
     */
    WorkspaceInvitationDTO selectInvitationByToken(@Param("token") String token);

    /**
     * 4. 초대 상태 변경
     * @param invitationId 초대 ID
     * @param status       변경할 상태 (REVOKED / EXPIRED)
     */
    void updateInvitationStatus(
            @Param("invitationId") String invitationId,
            @Param("status") InvitationStatus status
    );

    /**
     * 5. 초대 수락 (STATUS=ACCEPTED, ACCEPTED_USER_ID, ACCEPTED_DT 갱신)
     * @param invitationId 초대 ID
     * @param userId       수락한 사용자 ID
     */
    void acceptInvitation(
            @Param("invitationId") String invitationId,
            @Param("userId") String userId
    );

    /**
     * 6. 만료된 PENDING 초대 일괄 EXPIRED 처리 (lazy expire)
     * @param wsId 워크스페이스 ID
     */
    void expirePendingInvitations(@Param("wsId") String wsId);

    /**
     * 7. 워크스페이스 멤버 여부 확인 (수락 중복 방지)
     * @param wsId   워크스페이스 ID
     * @param userId 사용자 ID
     */
    boolean existsWorkspaceUser(
            @Param("wsId") String wsId,
            @Param("userId") String userId
    );
}
