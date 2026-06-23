"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button, Input, Label } from "@/shared/ui";
import { updateMe } from "@/shared/api";
import { ApiError } from "@/shared/api";
import { useAuthStore } from "@/stores/auth-store";
import { ChevronLeft } from "lucide-react";

export default function ProfileSetupPage() {
  const router = useRouter();
  const { currentWorkspace, setProfile } = useAuthStore();
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleStart = async () => {
    if (!name.trim()) return;
    setError("");
    setIsLoading(true);
    try {
      // PATCH /api/v1.0/user/me → 수정된 UserProfileDTO 반환
      const res = await updateMe({ name: name.trim() });
      setProfile(res.data); // 반환된 프로필로 store 업데이트 (userName 포함)
      router.push("/");
    } catch (err) {
      setError(
        err instanceof ApiError ? err.messages : "이름 저장에 실패했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-full flex flex-col items-center justify-center bg-background px-4 py-12">
      <div className="w-full max-w-sm">
        {/* Back */}
        <Button
          variant="ghost"
          size="sm"
          onClick={() => router.back()}
          className="mb-10 -ml-2 text-muted-foreground"
        >
          <ChevronLeft size={16} />
          뒤로
        </Button>

        {/* Step dots */}
        <div className="flex items-center gap-2 mb-8">
          <div className="w-2 h-2 rounded-full bg-primary" />
          <div className="w-2 h-2 rounded-full bg-primary" />
          <span className="ml-2 text-xs text-muted-foreground">2 / 2</span>
        </div>

        {/* Heading */}
        <div className="mb-8">
          {currentWorkspace && (
            <p className="text-xs font-semibold uppercase tracking-widest text-primary mb-2">
              {currentWorkspace.name}
            </p>
          )}
          <h2 className="text-2xl font-bold text-foreground leading-tight">
            이름을 알려주세요
          </h2>
          <p className="text-sm text-muted-foreground mt-2">
            워크스페이스에서 사용할 이름이에요
          </p>
        </div>

        {/* 에러 메시지 */}
        {error && (
          <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2.5 mb-4">
            <p className="text-xs text-destructive">{error}</p>
          </div>
        )}

        {/* Input */}
        <div className="space-y-1.5 mb-2">
          <Label htmlFor="user-name" variant="required">
            이름
          </Label>
          <Input
            id="user-name"
            placeholder="예: 이주, 엄마, 아빠"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleStart()}
            aria-invalid={!!error || undefined}
            disabled={isLoading}
            autoFocus
          />
        </div>
        <p className="text-xs text-muted-foreground mb-8">
          가족 구성원들이 이 이름으로 나를 알아볼 수 있어요
        </p>

        <Button
          size="lg"
          className="w-full font-semibold"
          onClick={handleStart}
          disabled={isLoading || !name.trim()}
        >
          {isLoading ? "저장 중..." : "시작하기"}
        </Button>
      </div>
    </div>
  );
}
