package com.terry.backend.api.workspace.mapper;

import com.terry.backend.api.workspace.dto.WorkspaceAuthority;
import com.terry.backend.api.workspace.dto.WorkspaceDTO;
import com.terry.backend.api.workspace.dto.WorkspaceMemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkspaceMapper {

    /**
     * 1. 워크스페이스 생성
     * @param workspace 생성할 워크스페이스 정보
     */
    void insertWorkspace(WorkspaceDTO workspace);

    /**
     * 2. 워크스페이스 멤버 추가
     * @param id        WORKSPACE_USER ID
     * @param wsId      워크스페이스 ID
     * @param userId    회원 ID
     * @param authority 권한 (OWNER/MEMBER)
     */
    void insertWorkspaceUser(
            @Param("id") String id,
            @Param("wsId") String wsId,
            @Param("userId") String userId,
            @Param("authority") WorkspaceAuthority authority
    );

    /**
     * 3. 내 워크스페이스 목록 조회
     * @param userId 회원 ID
     */
    List<WorkspaceDTO> selectMyWorkspaces(@Param("userId") String userId);

    /**
     * 4. 특정 워크스페이스 + 멤버십 확인 (멤버 아니면 null 반환)
     * @param workspaceId 워크스페이스 ID
     * @param userId      회원 ID
     */
    WorkspaceDTO selectWorkspaceWithAuthority(
            @Param("workspaceId") String workspaceId,
            @Param("userId") String userId
    );

    /**
     * 5. 워크스페이스 멤버 목록 조회
     * @param workspaceId 워크스페이스 ID
     */
    List<WorkspaceMemberDTO> selectWorkspaceMembers(@Param("workspaceId") String workspaceId);
}
