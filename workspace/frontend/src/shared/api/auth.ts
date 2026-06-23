import { apiFetch, type ApiResponse } from "./client";
import { useAuthStore } from "@/stores/auth-store";
import { getMe } from "./user";

interface LoginPayload {
  username: string;
  password: string;
}

export interface TokenData {
  accessToken: string;
  refreshToken: string;
}

/**
 * POST /login
 * 로그인 성공 시 토큰 저장 후 GET /api/v1.0/user/me를 호출하여
 * 사용자 프로필(name 등)을 store에 자동 로드합니다.
 */
export async function login(payload: LoginPayload): Promise<ApiResponse<TokenData>> {
  const res = await apiFetch<TokenData>("/login", {
    method: "POST",
    body: JSON.stringify(payload),
    skipAuth: true,
  });
  useAuthStore.getState().setTokens(res.data.accessToken, res.data.refreshToken);
  // 토큰 저장 직후 사용자 프로필 로드 (best-effort, 실패해도 로그인 흐름 유지)
  try {
    const meRes = await getMe();
    useAuthStore.getState().setProfile(meRes.data);
  } catch {
    // 프로필 로드 실패 시 온보딩에서 입력한 이름으로 폴백
  }
  return res;
}

/**
 * POST /token/refresh
 * Refresh Token으로 새 Access Token + Refresh Token을 발급받습니다.
 */
export async function refreshAccessToken(): Promise<ApiResponse<TokenData>> {
  const { refreshToken } = useAuthStore.getState();
  if (!refreshToken) {
    throw new Error("Refresh Token이 없습니다.");
  }
  const res = await apiFetch<TokenData>("/token/refresh", {
    method: "POST",
    headers: { "Refresh-Token": refreshToken },
    skipAuth: true,
  });
  useAuthStore.getState().setTokens(res.data.accessToken, res.data.refreshToken);
  return res;
}

/**
 * POST /logout
 * Refresh Token을 폐기하고 store를 초기화합니다.
 */
export async function logout(): Promise<void> {
  try {
    await apiFetch("/logout", { method: "POST" });
  } finally {
    useAuthStore.getState().reset();
  }
}
