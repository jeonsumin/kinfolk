import { apiFetch, type ApiResponse } from "./client";
import type { WorkspaceAuthority } from "./workspace";

export type InvitationStatus = "PENDING" | "ACCEPTED" | "EXPIRED" | "REVOKED";

export interface WorkspaceInvitationDTO {
  invitationId: string;
  wsId: string;
  wsNm: string | null;
  inviteEmail: string | null;
  inviteToken: string;
  authority: WorkspaceAuthority;
  status: InvitationStatus;
  expireDt: string;
  acceptedUserId: string | null;
  acceptedDt: string | null;
  registDt: string | null;
  registId: string | null;
  inviterName: string | null;
  inviteUrl: string;
}

export interface CreateInvitationRequest {
  email?: string;
  authority?: WorkspaceAuthority;
}

export interface UpdateMemberAuthorityRequest {
  authority: WorkspaceAuthority;
}

const BASE = "/api/v1.0";

/** POST /workspace/{wsId}/invitations — 이메일(email 있을 때) 또는 오픈링크 초대 생성 */
export async function createInvitation(
  wsId: string,
  payload: CreateInvitationRequest
): Promise<ApiResponse<WorkspaceInvitationDTO>> {
  return apiFetch<WorkspaceInvitationDTO>(`${BASE}/workspace/${wsId}/invitations`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

/** GET /workspace/{wsId}/invitations — 초대 목록 조회 (OWNER 전용) */
export async function getInvitations(
  wsId: string
): Promise<ApiResponse<WorkspaceInvitationDTO[]>> {
  return apiFetch<WorkspaceInvitationDTO[]>(`${BASE}/workspace/${wsId}/invitations`);
}

/** DELETE /workspace/{wsId}/invitations/{invitationId} — 초대 취소 */
export async function cancelInvitation(
  wsId: string,
  invitationId: string
): Promise<ApiResponse<unknown>> {
  return apiFetch<unknown>(
    `${BASE}/workspace/${wsId}/invitations/${invitationId}`,
    { method: "DELETE" }
  );
}

/** GET /invitations/{token} — 토큰으로 초대 정보 조회 (수락 페이지용) */
export async function getInvitationByToken(
  token: string
): Promise<ApiResponse<WorkspaceInvitationDTO>> {
  return apiFetch<WorkspaceInvitationDTO>(`${BASE}/invitations/${token}`);
}

/** POST /invitations/{token}/accept — 초대 수락 (현재 사용자를 멤버로 등록) */
export async function acceptInvitation(
  token: string
): Promise<ApiResponse<unknown>> {
  return apiFetch<unknown>(`${BASE}/invitations/${token}/accept`, {
    method: "POST",
  });
}

/** DELETE /workspace/{wsId}/members/{memberId} — 멤버 내보내기 (OWNER 전용) */
export async function removeMember(
  wsId: string,
  memberId: string
): Promise<ApiResponse<unknown>> {
  return apiFetch<unknown>(`${BASE}/workspace/${wsId}/members/${memberId}`, {
    method: "DELETE",
  });
}

/** PATCH /workspace/{wsId}/members/{memberId}/authority — 멤버 권한 변경 (OWNER 전용) */
export async function updateMemberAuthority(
  wsId: string,
  memberId: string,
  payload: UpdateMemberAuthorityRequest
): Promise<ApiResponse<unknown>> {
  return apiFetch<unknown>(
    `${BASE}/workspace/${wsId}/members/${memberId}/authority`,
    { method: "PATCH", body: JSON.stringify(payload) }
  );
}
