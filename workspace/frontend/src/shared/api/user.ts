import { apiFetch, type ApiResponse } from "./client";

/** GET /api/v1.0/user/me 응답 데이터 */
export interface UserProfile {
  id: string;
  loginId: string;
  name: string;
  email?: string;
  profileImageUri?: string | null;
  mobile?: string;
}

/** PATCH /api/v1.0/user/me 요청 바디 */
export interface UpdateUserPayload {
  displayName: string; // 필수, 최대 50자
}

const BASE = "/api/v1.0/user";

/**
 * GET /api/v1.0/user/me
 * 현재 로그인한 사용자 정보를 조회합니다.
 */
export async function getMe(): Promise<ApiResponse<UserProfile>> {
  return apiFetch<UserProfile>(`${BASE}/me`);
}

/**
 * PATCH /api/v1.0/user/me
 * 사용자 이름을 업데이트하고 수정된 UserProfileDTO를 반환합니다.
 */
export async function updateMe(
  payload: UpdateUserPayload
): Promise<ApiResponse<UserProfile>> {
  return apiFetch<UserProfile>(`${BASE}/me`, {
    method: "PATCH",
    body: JSON.stringify(payload),
  });
}
