package com.terry.backend.api.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class WorkspaceInvitationDTO {
    private static final long serialVersionUID = 1L;

    private String invitationId;
    private String wsId;
    private String wsNm;          // 워크스페이스명 (토큰 조회 시 JOIN)
    private String inviteEmail;
    private String inviteToken;
    private WorkspaceAuthority authority;
    private InvitationStatus status;
    private Date expireDt;
    private String acceptedUserId;
    private Date acceptedDt;
    private Date registDt;
    private String registId;      // 초대자 ID
    private String inviterName;   // 초대자 이름 (토큰 조회 시 JOIN)
    private String inviteUrl;     // 응답 전용 (DB 미저장)
}
