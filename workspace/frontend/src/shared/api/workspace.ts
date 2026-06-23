import { apiFetch, type ApiResponse } from "./client";

export type WorkspaceAuthority = "OWNER" | "MEMBER";

/** 서버 WorkspaceDTO */
export interface WorkspaceDTO {
  id: string;
  wsNm: string;
  wsDesc?: string | null;
  wsOwnerId: string;
  authority: WorkspaceAuthority;
  registDt: string;
}

/** POST /api/v1.0/workspace 요청 바디 */
export interface CreateWorkspacePayload {
  wsNm: string;       // 필수, 최대 255자
  wsDesc?: string;    // 선택
}

/** 워크스페이스 멤버 DTO */
export interface WorkspaceMemberDTO {
  memberId: string;
  name: string;
  profileImageUri?: string | null;
  authority: WorkspaceAuthority;
}

const BASE = "/api/v1.0/workspace";

/**
 * POST /api/v1.0/workspace
 * 새 워크스페이스를 생성합니다. (응답 201)
 */
export async function createWorkspace(
  payload: CreateWorkspacePayload
): Promise<ApiResponse<WorkspaceDTO>> {
  return apiFetch<WorkspaceDTO>(BASE, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

/**
 * GET /api/v1.0/workspace
 * 내가 속한 워크스페이스 목록을 조회합니다.
 */
export async function getWorkspaces(): Promise<ApiResponse<WorkspaceDTO[]>> {
  return apiFetch<WorkspaceDTO[]>(BASE);
}

/**
 * PATCH /api/v1.0/workspace/{workspaceId}/select
 * 워크스페이스를 전환합니다.
 * 서버가 멤버십을 검증하고 WorkspaceDTO를 반환합니다.
 * 반환값을 클라이언트 currentWorkspace에 보관해야 합니다.
 *
 * @throws ApiError (403: 멤버 아님 / 존재하지 않음)
 */
export async function selectWorkspace(
  workspaceId: string
): Promise<ApiResponse<WorkspaceDTO>> {
  return apiFetch<WorkspaceDTO>(`${BASE}/${workspaceId}/select`, {
    method: "PATCH",
  });
}

/**
 * GET /api/v1.0/workspace/{workspaceId}/members
 * 워크스페이스 멤버 목록을 조회합니다.
 * (장보기 담당자 선택 picker 등에서 사용)
 *
 * @throws ApiError (403: 멤버 아님 / 존재하지 않음)
 */
export async function getWorkspaceMembers(
  workspaceId: string
): Promise<ApiResponse<WorkspaceMemberDTO[]>> {
  return apiFetch<WorkspaceMemberDTO[]>(`${BASE}/${workspaceId}/members`);
}
