"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button, Input, Label, Avatar, AvatarFallback } from "@/shared/ui";
import { createWorkspace, getWorkspaces, selectWorkspace as selectWorkspaceApi } from "@/shared/api";
import { ApiError } from "@/shared/api";
import { useAuthStore, type Workspace } from "@/stores/auth-store";
import { Check, Plus, ChevronLeft } from "lucide-react";

export default function WorkspacesPage() {
  const router = useRouter();
  const { workspaces, currentWorkspace, setWorkspaces, setCurrentWorkspace } =
    useAuthStore();

  const [isCreating, setIsCreating] = useState(false);
  const [newName, setNewName] = useState("");
  const [createError, setCreateError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSwitching, setIsSwitching] = useState<string | null>(null);

  // 페이지 진입 시 서버에서 최신 목록 로드
  useEffect(() => {
    getWorkspaces()
      .then((res) => {
        const mapped = res.data.map((ws) => ({ id: ws.id, name: ws.wsNm }));
        setWorkspaces(mapped);
      })
      .catch(() => {
        // 실패 시 로컬 store 목록 유지
      });
  }, [setWorkspaces]);

  const handleSelect = async (ws: Workspace) => {
    if (isSwitching || currentWorkspace?.id === ws.id) return;
    setIsSwitching(ws.id);
    try {
      const res = await selectWorkspaceApi(ws.id);
      // 서버 반환 WorkspaceDTO → UI용 Workspace 매핑 후 currentWorkspace 저장
      setCurrentWorkspace({ id: res.data.id, name: res.data.wsNm });
      router.push("/");
    } catch {
      // 전환 실패 시 로컬 상태로 폴백
      setCurrentWorkspace(ws);
      router.push("/");
    } finally {
      setIsSwitching(null);
    }
  };

  const handleCreate = async () => {
    if (!newName.trim()) return;
    setCreateError("");
    setIsSubmitting(true);
    try {
      const res = await createWorkspace({ wsNm: newName.trim() });
      const newWs: Workspace = { id: res.data.id, name: res.data.wsNm };
      setWorkspaces([...workspaces, newWs]);
      setNewName("");
      setIsCreating(false);
    } catch (err) {
      setCreateError(
        err instanceof ApiError ? err.messages : "생성에 실패했습니다."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex flex-col flex-1 h-full overflow-hidden">
      <div className="flex-1 overflow-y-auto">
        <div className="max-w-2xl mx-auto px-4 lg:px-6 py-6 lg:py-8">

          {/* Header */}
          <div className="flex items-center gap-3 mb-6">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => router.back()}
              aria-label="뒤로"
            >
              <ChevronLeft size={18} />
            </Button>
            <div>
              <h1 className="text-xl font-bold text-foreground">워크스페이스</h1>
              <p className="text-sm text-muted-foreground">
                이용할 워크스페이스를 선택하세요
              </p>
            </div>
          </div>

          {/* Workspace list */}
          <div className="bg-card rounded-xl border border-border overflow-hidden mb-4">
            {workspaces.length === 0 ? (
              <div className="px-4 py-10 text-center">
                <p className="text-sm text-muted-foreground">
                  아직 워크스페이스가 없습니다
                </p>
              </div>
            ) : (
              <ul className="divide-y divide-border">
                {workspaces.map((ws) => {
                  const isActive = currentWorkspace?.id === ws.id;
                  const isLoading = isSwitching === ws.id;
                  return (
                    <li key={ws.id}>
                      <button
                        onClick={() => handleSelect(ws)}
                        disabled={!!isSwitching}
                        className="w-full flex items-center gap-3 px-4 py-4 hover:bg-muted/50 transition-colors text-left disabled:opacity-60"
                      >
                        <Avatar>
                          <AvatarFallback className="bg-secondary text-secondary-foreground font-semibold text-sm">
                            {ws.name.charAt(0)}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex-1 min-w-0">
                          <p className={`text-sm font-medium truncate ${isActive ? "text-primary" : "text-foreground"}`}>
                            {ws.name}
                          </p>
                          {isActive && !isLoading && (
                            <p className="text-xs text-muted-foreground mt-0.5">현재 워크스페이스</p>
                          )}
                          {isLoading && (
                            <p className="text-xs text-muted-foreground mt-0.5">전환 중...</p>
                          )}
                        </div>
                        {isActive && !isLoading && (
                          <Check size={16} className="text-primary shrink-0" strokeWidth={2.5} />
                        )}
                      </button>
                    </li>
                  );
                })}
              </ul>
            )}
          </div>

          {/* Create new workspace */}
          {isCreating ? (
            <div className="bg-card rounded-xl border border-border p-4 space-y-3">
              <p className="text-sm font-medium text-foreground">새 워크스페이스</p>
              {createError && (
                <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2">
                  <p className="text-xs text-destructive">{createError}</p>
                </div>
              )}
              <div className="space-y-1.5">
                <Label htmlFor="new-workspace-name">이름</Label>
                <Input
                  id="new-workspace-name"
                  placeholder="예: 우리가족"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleCreate()}
                  disabled={isSubmitting}
                  autoFocus
                />
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  className="flex-1"
                  onClick={() => { setIsCreating(false); setNewName(""); setCreateError(""); }}
                  disabled={isSubmitting}
                >
                  취소
                </Button>
                <Button
                  size="sm"
                  className="flex-1"
                  onClick={handleCreate}
                  disabled={isSubmitting || !newName.trim()}
                >
                  {isSubmitting ? "생성 중..." : "만들기"}
                </Button>
              </div>
            </div>
          ) : (
            <button
              onClick={() => setIsCreating(true)}
              className="w-full flex items-center gap-2 px-4 py-3 rounded-xl border border-dashed border-border text-sm text-muted-foreground hover:border-primary hover:text-primary transition-colors"
            >
              <Plus size={16} strokeWidth={2} />
              새 워크스페이스 만들기
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
