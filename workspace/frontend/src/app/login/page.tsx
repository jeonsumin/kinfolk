"use client";

import { Suspense, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { signIn, getSession } from "next-auth/react";
import { Button, Input, Label } from "@/shared/ui";
import { getMe, getWorkspaces } from "@/shared/api";
import { useAuthStore } from "@/stores/auth-store";

function getPostLoginPath(callbackUrl: string | null, hasWorkspaces: boolean) {
  const fallback = hasWorkspaces ? "/" : "/onboarding/workspace";
  if (!callbackUrl || !callbackUrl.startsWith("/") || callbackUrl.startsWith("//")) return fallback;
  if (callbackUrl === "/login" || callbackUrl === "/signup") return fallback;
  if (callbackUrl.startsWith("/login?") || callbackUrl.startsWith("/signup?")) return fallback;
  if (callbackUrl.startsWith("/onboarding")) return fallback;
  if (!hasWorkspaces && !callbackUrl.startsWith("/invite/")) return fallback;
  return callbackUrl;
}

function LoginContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { setTokens, setProfile, setWorkspaces, setCurrentWorkspace, reset } = useAuthStore();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async () => {
    if (!username.trim() || !password) return;
    setError("");
    setIsLoading(true);
    reset();

    try {
      // 1. NextAuth Credentials 로그인 → 서버에서 /login 호출 후 세션 쿠키 발급
      const result = await signIn("credentials", {
        redirect: false,
        username: username.trim(),
        password,
      });

      if (result?.error || !result?.ok) {
        setError("아이디 또는 비밀번호가 잘못됐습니다.");
        return;
      }

      // 2. 세션에서 토큰 읽어 Zustand에 동기화 (apiFetch 에서 사용)
      const session = await getSession();
      if (!session?.accessToken) {
        setError("아이디 또는 비밀번호가 잘못됐습니다.");
        return;
      }
      setTokens(session.accessToken, session.refreshToken ?? "");

      // 3. 사용자 프로필 로드
      try {
        const meRes = await getMe();
        setProfile(meRes.data);
      } catch {
        // 프로필 실패 시 온보딩 이름 입력으로 폴백
      }

      // 4. 워크스페이스 유무로 신규/기존 사용자 분기
      const wsRes = await getWorkspaces();
      const workspaces = wsRes.data;
      const callbackUrl = searchParams.get("callbackUrl");

      if (!workspaces || workspaces.length === 0) {
        router.push(getPostLoginPath(callbackUrl, false));
      } else {
        const mapped = workspaces.map((ws) => ({ id: ws.id, name: ws.wsNm }));
        setWorkspaces(mapped);
        setCurrentWorkspace(mapped[0]);
        router.push(getPostLoginPath(callbackUrl, true));
      }
    } catch {
      setError("로그인에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleLogin();
  };

  return (
    <div className="min-h-full flex flex-col items-center justify-center bg-background px-4 py-12">
      {/* Branding */}
      <div className="mb-8 text-center">
        <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-1">
          Kinfolk
        </p>
        <h1 className="text-3xl font-bold text-foreground">Table</h1>
        <p className="text-sm text-muted-foreground mt-1">가족을 위한 공간</p>
      </div>

      {/* Card */}
      <div className="w-full max-w-sm bg-card rounded-2xl border border-border p-6 space-y-5 shadow-sm">
        <div>
          <h2 className="text-xl font-semibold text-foreground">환영합니다</h2>
          <p className="text-sm text-muted-foreground mt-0.5">
            로그인하여 가족과 함께하세요
          </p>
        </div>

        {searchParams.get("registered") === "1" && (
          <div className="rounded-lg bg-primary/10 border border-primary/20 px-3 py-2.5">
            <p className="text-xs text-primary">회원가입이 완료되었습니다. 로그인해주세요.</p>
          </div>
        )}

        {/* 에러 메시지 */}
        {error && (
          <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2.5">
            <p className="text-xs text-destructive">{error}</p>
          </div>
        )}

        <div className="space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="username">아이디</Label>
            <Input
              id="username"
              type="text"
              placeholder="로그인 아이디 입력"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              onKeyDown={handleKeyDown}
              aria-invalid={!!error || undefined}
              disabled={isLoading}
              autoComplete="username"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="password">비밀번호</Label>
            <Input
              id="password"
              type="password"
              placeholder="비밀번호 입력"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onKeyDown={handleKeyDown}
              aria-invalid={!!error || undefined}
              disabled={isLoading}
              autoComplete="current-password"
            />
          </div>
        </div>

        <Button
          size="lg"
          className="w-full font-semibold"
          onClick={handleLogin}
          disabled={isLoading || !username.trim() || !password}
        >
          {isLoading ? "로그인 중..." : "로그인"}
        </Button>

        {/* Divider */}
        <div className="relative flex items-center">
          <div className="flex-1 border-t border-border" />
          <span className="mx-3 text-xs text-muted-foreground">또는</span>
          <div className="flex-1 border-t border-border" />
        </div>

        {/* Google 로그인 — OAuth 구현 후 활성화 예정 */}
        <Button
          variant="outline"
          size="lg"
          className="w-full font-medium"
          disabled
          title="Google 로그인은 준비 중입니다"
        >
          <GoogleIcon />
          Google로 로그인 (준비 중)
        </Button>

        <p className="text-center text-xs text-muted-foreground">
          계정이 없으신가요?{" "}
          <Button variant="link" size="xs" className="px-0 h-auto text-xs" onClick={() => {
              const cb = searchParams.get("callbackUrl");
              router.push(cb ? `/signup?callbackUrl=${encodeURIComponent(cb)}` : "/signup");
            }}>
            회원가입
          </Button>
        </p>
      </div>
    </div>
  );
}

export default function LoginPage() {
  return (
    <Suspense>
      <LoginContent />
    </Suspense>
  );
}

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" className="size-4 shrink-0" aria-hidden="true">
      <path
        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
        fill="#4285F4"
      />
      <path
        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
        fill="#34A853"
      />
      <path
        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
        fill="#FBBC05"
      />
      <path
        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
        fill="#EA4335"
      />
    </svg>
  );
}
