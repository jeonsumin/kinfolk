"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button, Input, Label } from "@/shared/ui";
import { createWorkspace } from "@/shared/api";
import { ApiError } from "@/shared/api";
import { useAuthStore } from "@/stores/auth-store";
import { ChevronLeft } from "lucide-react";

export default function WorkspaceSetupPage() {
  const router = useRouter();
  const { addWorkspace } = useAuthStore();
  const [name, setName] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleNext = async () => {
    if (!name.trim()) return;
    setError("");
    setIsLoading(true);
    try {
      const res = await createWorkspace({ wsNm: name.trim() });
      // WorkspaceDTO → UI용 Workspace로 매핑
      addWorkspace({ id: res.data.id, name: res.data.wsNm });
      router.push("/onboarding/profile");
    } catch (err) {
      setError(
        err instanceof ApiError ? err.messages : "워크스페이스 생성에 실패했습니다."
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
          <div className="w-2 h-2 rounded-full bg-border" />
          <span className="ml-2 text-xs text-muted-foreground">1 / 2</span>
        </div>

        {/* Heading */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-foreground leading-tight">
            워크스페이스 이름을
            <br />
            알려주세요
          </h2>
          <p className="text-sm text-muted-foreground mt-2">
            함께할 그룹의 이름을 입력해주세요
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
          <Label htmlFor="workspace-name" variant="required">
            워크스페이스 이름
          </Label>
          <Input
            id="workspace-name"
            placeholder="예: 우리가족, 스미스 일가"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleNext()}
            aria-invalid={!!error || undefined}
            disabled={isLoading}
            autoFocus
          />
        </div>
        <p className="text-xs text-muted-foreground mb-8">
          나중에 설정에서 변경할 수 있어요
        </p>

        <Button
          size="lg"
          className="w-full font-semibold"
          onClick={handleNext}
          disabled={isLoading || !name.trim()}
        >
          {isLoading ? "생성 중..." : "다음"}
        </Button>
      </div>
    </div>
  );
}
