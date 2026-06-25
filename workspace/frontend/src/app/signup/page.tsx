"use client";

import { Suspense, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { ApiError, signup } from "@/shared/api";
import {
  Button,
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
  Input,
  Label,
} from "@/shared/ui";

function SignupContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const callbackUrl = searchParams.get("callbackUrl") ?? "";

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      await signup({ username: username.trim(), password, name: name.trim() });
      const redirect = callbackUrl
        ? `/login?registered=1&callbackUrl=${encodeURIComponent(callbackUrl)}`
        : "/login?registered=1";
      router.replace(redirect);
    } catch (error) {
      setError(error instanceof ApiError ? error.messages : "회원가입에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoLogin = () => {
    router.push(callbackUrl ? `/login?callbackUrl=${encodeURIComponent(callbackUrl)}` : "/login");
  };

  return (
    <div className="min-h-full flex flex-col items-center justify-center bg-background px-4 py-12">
      <div className="mb-8 text-center">
        <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-1">Kinfolk</p>
        <h1 className="text-3xl font-bold text-foreground">Table</h1>
        <p className="text-sm text-muted-foreground mt-1">가족을 위한 공간</p>
      </div>

      <form onSubmit={handleSubmit} className="w-full max-w-sm">
        <Card>
          <CardHeader>
            <CardTitle>회원가입</CardTitle>
            <CardDescription>가족과 함께할 계정을 만들어보세요</CardDescription>
          </CardHeader>
          <CardContent className="space-y-5">
            {error && (
              <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2.5">
                <p className="text-xs text-destructive">{error}</p>
              </div>
            )}

            <div className="space-y-4">
              <div className="space-y-1.5">
                <Label variant="required" htmlFor="username">아이디</Label>
                <Input id="username" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="영문, 숫자, . _ - (3~50자)" autoComplete="username" minLength={3} maxLength={50} pattern="[a-zA-Z0-9._-]+" required disabled={isLoading} />
              </div>
              <div className="space-y-1.5">
                <Label variant="required" htmlFor="password">비밀번호</Label>
                <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호 입력" autoComplete="new-password" maxLength={100} required disabled={isLoading} />
              </div>
              <div className="space-y-1.5">
                <Label variant="required" htmlFor="name">이름</Label>
                <Input id="name" value={name} onChange={(e) => setName(e.target.value)} placeholder="이름 입력" autoComplete="name" maxLength={50} required disabled={isLoading} />
              </div>
            </div>
          </CardContent>
          <CardFooter className="flex-col gap-3">
            <Button type="submit" size="lg" className="w-full font-semibold" disabled={isLoading}>
              {isLoading ? "가입 중..." : "회원가입"}
            </Button>
            <p className="text-center text-xs text-muted-foreground">
              이미 계정이 있으신가요?{" "}
              <Button type="button" variant="link" size="xs" className="px-0 h-auto text-xs" onClick={handleGoLogin}>
                로그인
              </Button>
            </p>
          </CardFooter>
        </Card>
      </form>
    </div>
  );
}

export default function SignupPage() {
  return (
    <Suspense>
      <SignupContent />
    </Suspense>
  );
}
