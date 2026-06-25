"use client";

import {useEffect, useState} from "react";
import {useParams, useRouter} from "next/navigation";
import {Avatar, AvatarFallback, Button} from "@/shared/ui";
import {
    ApiError,
    acceptInvitation,
    getInvitationByToken,
    getWorkspaces,
    type WorkspaceInvitationDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";

type PageState =
    | {kind: "loading"}
    | {kind: "info"; invitation: WorkspaceInvitationDTO}
    | {kind: "invalid"; message: string}
    | {kind: "done"};

export default function InvitePage() {
    const params = useParams();
    const token = params.token as string;
    const router = useRouter();
    const {isLoggedIn, setWorkspaces, setCurrentWorkspace} = useAuthStore();

    const [state, setState] = useState<PageState>({kind: "loading"});
    const [accepting, setAccepting] = useState(false);
    const [acceptError, setAcceptError] = useState("");

    useEffect(() => {
        if (!isLoggedIn) {
            router.replace(`/login?callbackUrl=/invite/${token}`);
            return;
        }

        getInvitationByToken(token)
            .then((res) => {
                const inv = res.data;
                if (inv.status === "EXPIRED") {
                    setState({kind: "invalid", message: "초대 링크가 만료되었습니다."});
                } else if (inv.status === "REVOKED") {
                    setState({kind: "invalid", message: "취소된 초대 링크입니다."});
                } else if (inv.status === "ACCEPTED") {
                    setState({kind: "invalid", message: "이미 수락된 초대입니다."});
                } else {
                    setState({kind: "info", invitation: inv});
                }
            })
            .catch((err) => {
                const message =
                    err instanceof ApiError && err.messages.includes("(404)")
                        ? "유효하지 않은 초대 링크입니다."
                        : "초대 정보를 불러오지 못했습니다.";
                setState({kind: "invalid", message});
            });
    }, [token, isLoggedIn, router]);

    const handleAccept = async () => {
        setAcceptError("");
        setAccepting(true);
        try {
            await acceptInvitation(token);

            // 워크스페이스 목록 갱신 후 해당 워크스페이스로 전환
            const wsRes = await getWorkspaces();
            const mapped = wsRes.data.map((ws) => ({id: ws.id, name: ws.wsNm}));
            setWorkspaces(mapped);

            if (state.kind === "info") {
                const joined = mapped.find((ws) => ws.id === state.invitation.wsId);
                if (joined) setCurrentWorkspace(joined);
            }

            setState({kind: "done"});
            router.push("/");
        } catch (err) {
            if (err instanceof ApiError) {
                if (err.messages.includes("(409)")) {
                    // 이미 멤버인 경우 — 바로 이동
                    router.push("/");
                    return;
                }
                if (err.messages.includes("(410)")) {
                    setState({kind: "invalid", message: "만료되거나 취소된 초대입니다."});
                    return;
                }
                setAcceptError(err.messages);
            } else {
                setAcceptError("수락 처리 중 오류가 발생했습니다.");
            }
        } finally {
            setAccepting(false);
        }
    };

    if (state.kind === "loading") {
        return (
            <div className="flex min-h-screen items-center justify-center bg-background">
                <p className="text-sm text-muted-foreground">초대 정보를 불러오는 중...</p>
            </div>
        );
    }

    if (state.kind === "invalid" || state.kind === "done") {
        return (
            <div className="flex min-h-screen flex-col items-center justify-center bg-background px-4">
                <div className="w-full max-w-sm rounded-2xl border border-border bg-card p-6 text-center shadow-sm space-y-4">
                    <p className="text-sm font-medium text-foreground">
                        {state.kind === "done" ? "워크스페이스에 참여했습니다." : state.message}
                    </p>
                    <Button className="w-full" onClick={() => router.push("/")}>
                        홈으로 이동
                    </Button>
                </div>
            </div>
        );
    }

    const inv = state.invitation;

    return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-background px-4">
            <div className="w-full max-w-sm rounded-2xl border border-border bg-card p-6 shadow-sm space-y-6">
                {/* 헤더 */}
                <div className="text-center">
                    <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground mb-1">
                        Kinfolk
                    </p>
                    <h1 className="text-xl font-bold text-foreground">워크스페이스 초대</h1>
                </div>

                {/* 워크스페이스 정보 */}
                <div className="rounded-xl bg-muted/60 p-4 text-center space-y-2">
                    <Avatar className="mx-auto size-14">
                        <AvatarFallback className="text-xl font-bold bg-primary text-primary-foreground">
                            {(inv.wsNm ?? "?").charAt(0)}
                        </AvatarFallback>
                    </Avatar>
                    <p className="font-semibold text-foreground">{inv.wsNm ?? "워크스페이스"}</p>
                    {inv.inviterName && (
                        <p className="text-xs text-muted-foreground">
                            <span className="font-medium text-foreground">{inv.inviterName}</span>님이 초대했습니다
                        </p>
                    )}
                    {inv.inviteEmail && (
                        <p className="text-xs text-muted-foreground">
                            초대된 이메일: {inv.inviteEmail}
                        </p>
                    )}
                    <p className="text-xs text-muted-foreground">
                        만료: {new Date(inv.expireDt).toLocaleDateString("ko-KR")}
                    </p>
                </div>

                {acceptError && (
                    <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2.5">
                        <p className="text-xs text-destructive">{acceptError}</p>
                    </div>
                )}

                <Button className="w-full font-semibold" onClick={handleAccept} disabled={accepting}>
                    {accepting ? "참여 중..." : "워크스페이스 참여하기"}
                </Button>

                <p className="text-center text-xs text-muted-foreground">
                    참여하면 워크스페이스 멤버로 등록됩니다.
                </p>
            </div>
        </div>
    );
}
