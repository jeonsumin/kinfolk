package com.terry.backend.api.workspace.service;

import com.terry.backend.api.workspace.dto.CreateWorkspaceRequest;
import com.terry.backend.api.workspace.dto.WorkspaceAuthority;
import com.terry.backend.api.workspace.dto.WorkspaceDTO;
import com.terry.backend.api.workspace.dto.WorkspaceMemberDTO;
import com.terry.backend.api.workspace.mapper.WorkspaceMapper;
import com.terry.backend.api.workspace.strategy.WorkspaceStrategy;
import com.terry.backend.api.workspace.strategy.WorkspaceUserStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private static final SerialConfiguration<String> WS_STRATEGY   = new WorkspaceStrategy();
    private static final SerialConfiguration<String> WU_STRATEGY    = new WorkspaceUserStrategy();

    private final WorkspaceMapper mapper;

    /**
     * 워크스페이스 생성 + 소유자 멤버 등록 (트랜잭션)
     */
    @Transactional
    public WorkspaceDTO createWorkspace(CreateWorkspaceRequest request) throws Exception {
        String userId = SessionUtils.getUserId();

        String wsId = SerialUtil.get(WorkspaceStrategy.ID, WS_STRATEGY);
        String wuId = SerialUtil.get(WorkspaceUserStrategy.ID, WU_STRATEGY);

        WorkspaceDTO workspace = WorkspaceDTO.builder()
                .id(wsId)
                .wsNm(request.getWsNm())
                .wsDesc(request.getWsDesc())
                .wsOwnerId(userId)
                .build();

        mapper.insertWorkspace(workspace);
        mapper.insertWorkspaceUser(wuId, wsId, userId, WorkspaceAuthority.OWNER);

        workspace.setAuthority(WorkspaceAuthority.OWNER);
        return workspace;
    }

    /**
     * 내 워크스페이스 목록 조회
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDTO> getMyWorkspaces() {
        String userId = SessionUtils.getUserId();
        return mapper.selectMyWorkspaces(userId);
    }

    /**
     * 워크스페이스 전환 — 멤버십 검증 후 컨텍스트 반환
     */
    @Transactional(readOnly = true)
    public WorkspaceDTO selectWorkspace(String workspaceId) throws SystemException {
        String userId = SessionUtils.getUserId();
        WorkspaceDTO workspace = mapper.selectWorkspaceWithAuthority(workspaceId, userId);
        if (workspace == null) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
        return workspace;
    }

    /**
     * 워크스페이스 멤버 목록 조회 — 멤버십 검증 후 반환
     */
    @Transactional(readOnly = true)
    public List<WorkspaceMemberDTO> getWorkspaceMembers(String workspaceId) throws SystemException {
        // 현재 사용자가 멤버인지 검증 (기존 메서드 재사용)
        selectWorkspace(workspaceId);
        return mapper.selectWorkspaceMembers(workspaceId);
    }
}
