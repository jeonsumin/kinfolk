package com.terry.backend.api.workspace.controller;

import com.terry.backend.api.workspace.dto.CreateInvitationRequest;
import com.terry.backend.api.workspace.dto.WorkspaceInvitationDTO;
import com.terry.backend.api.workspace.service.WorkspaceInvitationService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "WorkspaceInvitation", description = "워크스페이스 초대 API")
public class WorkspaceInvitationController extends ApiRestController {

    private final WorkspaceInvitationService service;

    @PostMapping("/workspace/{wsId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "초대 생성", description = "OWNER 전용. email 있으면 이메일 초대, 없으면 오픈링크 토큰 발급. 응답에 inviteUrl 포함.")
    public WorkspaceInvitationDTO createInvitation(
            @PathVariable String wsId,
            @Valid @RequestBody CreateInvitationRequest request
    ) throws Exception {
        return service.createInvitation(wsId, request);
    }

    @GetMapping("/workspace/{wsId}/invitations")
    @Operation(summary = "초대 목록 조회", description = "OWNER 전용. 보낸 초대 목록을 반환한다. 만료된 PENDING은 EXPIRED로 갱신 후 반환.")
    public List<WorkspaceInvitationDTO> getInvitations(@PathVariable String wsId) throws Exception {
        return service.getInvitations(wsId);
    }

    @DeleteMapping("/workspace/{wsId}/invitations/{invitationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "초대 취소", description = "OWNER 전용. 초대 상태를 REVOKED로 변경한다.")
    public void revokeInvitation(
            @PathVariable String wsId,
            @PathVariable String invitationId
    ) throws Exception {
        service.revokeInvitation(wsId, invitationId);
    }

    @GetMapping("/invitations/{token}")
    @Operation(summary = "초대 토큰 조회", description = "토큰 유효성 및 워크스페이스 정보 반환. 수락 페이지 진입 시 호출.")
    public WorkspaceInvitationDTO getInvitationByToken(@PathVariable String token) throws Exception {
        return service.getInvitationByToken(token);
    }

    @PostMapping("/invitations/{token}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "초대 수락", description = "현재 사용자를 워크스페이스 멤버로 등록한다. 이미 멤버이면 멱등 처리(204 반환).")
    public void acceptInvitation(@PathVariable String token) throws Exception {
        service.acceptInvitation(token);
    }
}
