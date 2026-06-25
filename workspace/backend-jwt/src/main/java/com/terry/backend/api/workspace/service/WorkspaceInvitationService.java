package com.terry.backend.api.workspace.service;

import com.terry.backend.api.workspace.dto.CreateInvitationRequest;
import com.terry.backend.api.workspace.dto.InvitationStatus;
import com.terry.backend.api.workspace.dto.WorkspaceAuthority;
import com.terry.backend.api.workspace.dto.WorkspaceInvitationDTO;
import com.terry.backend.api.workspace.mapper.WorkspaceInvitationMapper;
import com.terry.backend.api.workspace.mapper.WorkspaceMapper;
import com.terry.backend.api.workspace.strategy.WorkspaceInvitationStrategy;
import com.terry.backend.api.workspace.strategy.WorkspaceUserStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.mail.dto.MailDTO;
import com.terry.backend.core.mail.service.MailService;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceInvitationService {

    private static final SerialConfiguration<String> WI_STRATEGY = new WorkspaceInvitationStrategy();
    private static final SerialConfiguration<String> WU_STRATEGY = new WorkspaceUserStrategy();

    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceInvitationMapper invitationMapper;
    private final WorkspaceService workspaceService;
    private final MailService mailService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * 초대 생성 (링크 또는 이메일)
     */
    @Transactional
    public WorkspaceInvitationDTO createInvitation(String wsId, CreateInvitationRequest request) throws Exception {
        workspaceService.requireOwner(wsId);

        String inviterId = SessionUtils.getUserId();
        String invitationId = SerialUtil.get(WorkspaceInvitationStrategy.ID, WI_STRATEGY);
        String inviteToken = UUID.randomUUID().toString().replace("-", "");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date expireDt = cal.getTime();

        WorkspaceInvitationDTO dto = WorkspaceInvitationDTO.builder()
                .invitationId(invitationId)
                .wsId(wsId)
                .inviteEmail(request.getEmail())
                .inviteToken(inviteToken)
                .authority(request.getAuthority() != null ? request.getAuthority() : WorkspaceAuthority.MEMBER)
                .status(InvitationStatus.PENDING)
                .expireDt(expireDt)
                .registId(inviterId)
                .build();

        invitationMapper.insertInvitation(dto);

        String inviteUrl = frontendUrl + "/invite/" + inviteToken;
        dto.setInviteUrl(inviteUrl);

        // 이메일 초대인 경우 메일 발송 (mail 설정 미완료 시 로그만 남김)
        if (StringUtils.hasText(request.getEmail())) {
            trySendInvitationMail(request.getEmail(), wsId, inviteUrl);
        }

        return dto;
    }

    /**
     * 초대 목록 조회 (OWNER 전용) — 만료 lazy 처리 후 반환
     */
    @Transactional
    public List<WorkspaceInvitationDTO> getInvitations(String wsId) throws SystemException {
        workspaceService.requireOwner(wsId);
        invitationMapper.expirePendingInvitations(wsId);
        List<WorkspaceInvitationDTO> list = invitationMapper.selectInvitationsByWsId(wsId);
        // inviteUrl 채우기 (DB 비저장 필드)
        list.forEach(i -> i.setInviteUrl(frontendUrl + "/invite/" + i.getInviteToken()));
        return list;
    }

    /**
     * 초대 취소 (OWNER 전용) — STATUS=REVOKED
     */
    @Transactional
    public void revokeInvitation(String wsId, String invitationId) throws SystemException {
        workspaceService.requireOwner(wsId);
        // wsId 귀속 확인: 목록 조회 후 매칭되는 초대인지 검증
        List<WorkspaceInvitationDTO> invitations = invitationMapper.selectInvitationsByWsId(wsId);
        boolean belongs = invitations.stream()
                .anyMatch(i -> invitationId.equals(i.getInvitationId()));
        if (!belongs) {
            throw new SystemException(HttpStatus.NOT_FOUND, "해당 초대를 찾을 수 없습니다.");
        }
        invitationMapper.updateInvitationStatus(invitationId, InvitationStatus.REVOKED);
    }

    /**
     * 토큰으로 초대 정보 조회 (수락 페이지용) — lazy 만료 처리 포함
     */
    @Transactional
    public WorkspaceInvitationDTO getInvitationByToken(String token) throws SystemException {
        WorkspaceInvitationDTO invitation = invitationMapper.selectInvitationByToken(token);
        if (invitation == null) {
            throw new SystemException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다.");
        }
        // lazy 만료 처리
        if (invitation.getStatus() == InvitationStatus.PENDING
                && invitation.getExpireDt() != null
                && invitation.getExpireDt().before(new Date())) {
            invitationMapper.updateInvitationStatus(invitation.getInvitationId(), InvitationStatus.EXPIRED);
            invitation.setStatus(InvitationStatus.EXPIRED);
        }
        invitation.setInviteUrl(frontendUrl + "/invite/" + token);
        return invitation;
    }

    /**
     * 초대 수락 — 현재 사용자를 WORKSPACE_USER에 등록
     * 이메일 초대인 경우 수락자 이메일 일치 여부와 무관하게 허용 (오픈링크 동일 정책)
     */
    @Transactional
    public void acceptInvitation(String token) throws Exception {
        WorkspaceInvitationDTO invitation = invitationMapper.selectInvitationByToken(token);
        if (invitation == null) {
            throw new SystemException(HttpStatus.NOT_FOUND, "유효하지 않은 초대 링크입니다.");
        }

        // 만료 체크 (lazy)
        if (invitation.getStatus() == InvitationStatus.PENDING
                && invitation.getExpireDt() != null
                && invitation.getExpireDt().before(new Date())) {
            invitationMapper.updateInvitationStatus(invitation.getInvitationId(), InvitationStatus.EXPIRED);
            invitation.setStatus(InvitationStatus.EXPIRED);
        }

        if (invitation.getStatus() == InvitationStatus.EXPIRED) {
            throw new SystemException(HttpStatus.GONE, "만료된 초대 링크입니다.");
        }
        if (invitation.getStatus() == InvitationStatus.REVOKED) {
            throw new SystemException(HttpStatus.GONE, "취소된 초대 링크입니다.");
        }
        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new SystemException(HttpStatus.CONFLICT, "이미 수락된 초대입니다.");
        }

        String userId = SessionUtils.getUserId();

        // 이미 멤버인 경우 멱등 처리 (재수락 시도 무시)
        if (invitationMapper.existsWorkspaceUser(invitation.getWsId(), userId)) {
            return;
        }

        String wuId = SerialUtil.get(WorkspaceUserStrategy.ID, WU_STRATEGY);
        workspaceMapper.insertWorkspaceUser(wuId, invitation.getWsId(), userId, invitation.getAuthority());
        invitationMapper.acceptInvitation(invitation.getInvitationId(), userId);
    }

    private void trySendInvitationMail(String toEmail, String wsId, String inviteUrl) {
        // ponytail: MailService 미구현 stub. mail 설정 완료 시 sendMail 구현 후 아래 주석 해제.
        log.info("[Invitation] 초대 메일 전송 대상: to={}, wsId={}, url={}", toEmail, wsId, inviteUrl);
        /*
        try {
            MailDTO mail = MailDTO.builder()
                    .toMail(toEmail)
                    .toName(toEmail)
                    .subject("[Kinfolk] 워크스페이스 초대")
                    .content("<p>초대 링크: <a href=\"" + inviteUrl + "\">" + inviteUrl + "</a></p>")
                    .build();
            mailService.sendMail(mail);
        } catch (Exception e) {
            log.warn("[Invitation] 초대 메일 발송 실패 (무시): to={}, error={}", toEmail, e.getMessage());
        }
        */
    }
}
