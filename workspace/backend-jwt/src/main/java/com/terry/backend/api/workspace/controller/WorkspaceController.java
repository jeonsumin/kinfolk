package com.terry.backend.api.workspace.controller;

import com.terry.backend.api.workspace.dto.CreateWorkspaceRequest;
import com.terry.backend.api.workspace.dto.UpdateMemberAuthorityRequest;
import com.terry.backend.api.workspace.dto.WorkspaceDTO;
import com.terry.backend.api.workspace.dto.WorkspaceMemberDTO;
import com.terry.backend.api.workspace.service.WorkspaceService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Workspace", description = "워크스페이스 API")
public class WorkspaceController extends ApiRestController {

    private final WorkspaceService service;

    @PostMapping("/workspace")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "워크스페이스 생성", description = "새 워크스페이스를 생성하고 현재 사용자를 OWNER로 등록한다.")
    public WorkspaceDTO createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) throws Exception {
        return service.createWorkspace(request);
    }

    @GetMapping("/workspace")
    @Operation(summary = "내 워크스페이스 목록", description = "현재 사용자가 속한 워크스페이스 목록을 반환한다.")
    public List<WorkspaceDTO> getMyWorkspaces() {
        return service.getMyWorkspaces();
    }

    @PatchMapping("/workspace/{workspaceId}/select")
    @Operation(summary = "워크스페이스 전환", description = "지정한 워크스페이스의 멤버십을 검증하고 컨텍스트를 반환한다.")
    public WorkspaceDTO selectWorkspace(@PathVariable String workspaceId) throws Exception {
        return service.selectWorkspace(workspaceId);
    }

    @GetMapping("/workspace/{workspaceId}/members")
    @Operation(summary = "워크스페이스 멤버 목록", description = "워크스페이스 멤버 목록을 반환한다. 현재 사용자가 멤버인지 검증한다.")
    public List<WorkspaceMemberDTO> getWorkspaceMembers(@PathVariable String workspaceId) throws Exception {
        return service.getWorkspaceMembers(workspaceId);
    }

    @DeleteMapping("/workspace/{workspaceId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "멤버 내보내기", description = "OWNER 전용. 워크스페이스에서 지정 멤버를 제거한다. 마지막 OWNER 삭제 불가.")
    public void removeMember(@PathVariable String workspaceId, @PathVariable String memberId) throws Exception {
        service.removeMember(workspaceId, memberId);
    }

    @PatchMapping("/workspace/{workspaceId}/members/{memberId}/authority")
    @Operation(summary = "멤버 권한 변경", description = "OWNER 전용. 지정 멤버의 권한을 변경한다. 마지막 OWNER 강등 불가.")
    public void updateMemberAuthority(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @Valid @RequestBody UpdateMemberAuthorityRequest request
    ) throws Exception {
        service.updateMemberAuthority(workspaceId, memberId, request.getAuthority());
    }
}
