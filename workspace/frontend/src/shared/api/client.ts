/**
 * Base API client
 *
 * NEXT_PUBLIC_API_URL 환경 변수로 백엔드 주소를 지정합니다.
 * 미설정 시 http://localhost:8080 을 사용합니다.
 *
 * .env.local 예시:
 *   NEXT_PUBLIC_API_URL=http://localhost:8080
 */

import { useAuthStore } from "@/stores/auth-store";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export interface ApiResponse<T = unknown> {
  success: boolean;
  data: T;
  messages: string;
}

export class ApiError extends Error {
  constructor(public readonly messages: string) {
    super(messages);
    this.name = "ApiError";
  }
}

/**
 * 인증 헤더를 자동으로 추가하는 fetch 래퍼입니다.
 *
 * 응답 형식 처리:
 * - 빈 body(204 No Content 등) → { success: true, data: {} }
 * - ResponseMessages 래핑({ success, data, messages }) → 그대로 처리
 * - 래핑 없는 raw JSON → { success: true, data: rawObject } 로 정규화
 * - success: false → ApiError throw
 */
export async function apiFetch<T = unknown>(
  path: string,
  options: RequestInit & { skipAuth?: boolean } = {}
): Promise<ApiResponse<T>> {
  const { accessToken } = useAuthStore.getState();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (!options.skipAuth && accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }

  const { skipAuth, ...fetchOptions } = options;

  let res: Response;
  try {
    res = await fetch(`${API_BASE}${path}`, { ...fetchOptions, headers });
  } catch {
    throw new ApiError("서버에 연결할 수 없습니다. 네트워크를 확인해주세요.");
  }

  // 401/403/500 → 로그인 리디렉션 (skipAuth 요청 제외)
  if (!skipAuth && (res.status === 401 || res.status === 403 || res.status === 500)) {
    if (res.status === 401) useAuthStore.getState().reset();
    if (typeof window !== "undefined") window.location.replace("/login");
    const statusMessages: Record<number, string> = {
      401: "인증이 만료되었습니다. 다시 로그인해주세요.",
      403: "접근 권한이 없습니다.",
      500: "서버 오류가 발생했습니다.",
    };
    throw new ApiError(statusMessages[res.status] ?? `요청에 실패했습니다. (${res.status})`);
  }

  // 빈 body 처리 (204 No Content 또는 body 없음)
  const text = await res.text();
  if (!text) {
    if (!res.ok) {
      throw new ApiError(`요청에 실패했습니다. (${res.status})`);
    }
    return { success: true, data: {} as T, messages: "" };
  }

  let parsed: unknown;
  try {
    parsed = JSON.parse(text);
  } catch {
    throw new ApiError("서버 응답을 처리할 수 없습니다.");
  }

  // ResponseMessages 래핑 여부 판별
  if (
    parsed !== null &&
    typeof parsed === "object" &&
    "success" in (parsed as object)
  ) {
    const body = parsed as ApiResponse<T>;
    if (!body.success) {
      throw new ApiError(body.messages || "요청에 실패했습니다.");
    }
    return body;
  }

  // 래핑 없는 raw JSON 응답 정규화
  if (!res.ok) {
    throw new ApiError(`요청에 실패했습니다. (${res.status})`);
  }
  return { success: true, data: parsed as T, messages: "" };
}
