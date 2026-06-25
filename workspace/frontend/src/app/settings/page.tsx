"use client";

import {useEffect, useState} from "react";
import {Bell, Camera, Copy, Link2, Mail, Palette, Shield, UserRound, Users, X} from "lucide-react";
import {Avatar, AvatarFallback, AvatarImage, Badge, Button, Card, CardContent, Input, Label, Switch} from "@/shared/ui";
import {
    ApiError,
    cancelInvitation,
    createInvitation,
    getInvitations,
    getWorkspaceMembers,
    removeMember,
    updateMemberAuthority,
    type InvitationStatus,
    type WorkspaceInvitationDTO,
    type WorkspaceMemberDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";
import {cn} from "@/shared/utils";

type SettingsMember = WorkspaceMemberDTO & {participationRate: number};

const MOCK_SETTINGS = {
    profile: {
        name: "김민지",
        email: "minji@kinfolk.com",
        profileImageUri: null,
    },
    workspace: {
        name: "우리 가족",
    },
    themes: [
        {id: "slate", name: "슬레이트", color: "#475569", selected: true},
        {id: "sage", name: "세이지", color: "#A3B18A", selected: false},
        {id: "rose", name: "로즈", color: "#D8A7B1", selected: false},
        {id: "sky", name: "스카이", color: "#B8C0FF", selected: false},
    ],
    notifications: [
        {id: "push", title: "푸시 알림", description: "모바일 기기로 실시간 알림을 보냅니다.", enabled: true},
        {id: "email", title: "이메일 알림", description: "중요한 업데이트를 메일로 받아봅니다.", enabled: false},
        {id: "calendar", title: "공유 캘린더 업데이트", description: "가족이 일정을 추가하거나 수정할 때 알림", enabled: true},
    ],
    members: [
        {memberId: "mom", name: "엄마", profileImageUri: null, authority: "OWNER", participationRate: 80},
        {memberId: "dad", name: "아빠", profileImageUri: null, authority: "MEMBER", participationRate: 65},
        {memberId: "child", name: "민지", profileImageUri: null, authority: "MEMBER", participationRate: 50},
    ],
} satisfies {
    profile: {name: string; email: string; profileImageUri: string | null};
    workspace: {name: string};
    themes: {id: string; name: string; color: string; selected: boolean}[];
    notifications: {id: string; title: string; description: string; enabled: boolean}[];
    members: SettingsMember[];
};

const toSettingsMembers = (members: WorkspaceMemberDTO[]): SettingsMember[] =>
    members.map((member, index) => ({
        ...member,
        participationRate: Math.max(35, 80 - index * 15),
    }));

const STATUS_LABEL: Record<InvitationStatus, string> = {
    PENDING: "대기중",
    ACCEPTED: "수락됨",
    EXPIRED: "만료됨",
    REVOKED: "취소됨",
};

const STATUS_VARIANT: Record<InvitationStatus, "default" | "secondary" | "outline" | "destructive"> = {
    PENDING: "secondary",
    ACCEPTED: "default",
    EXPIRED: "outline",
    REVOKED: "destructive",
};

/** ApiError 상태코드를 사용자 친화 메시지로 변환 */
function toFriendlyError(err: unknown): string {
    if (!(err instanceof ApiError)) return "오류가 발생했습니다.";
    const msg = err.messages;
    if (msg.includes("(409)")) return "마지막 관리자는 변경하거나 삭제할 수 없습니다.";
    if (msg.includes("(410)")) return "만료되거나 취소된 초대입니다.";
    if (msg.includes("(404)")) return "해당 멤버 또는 초대를 찾을 수 없습니다.";
    return msg;
}

export default function SettingsPage() {
    const {currentWorkspace, profile, userName} = useAuthStore();
    const [tab, setTab] = useState<"basic" | "members">("basic");

    // Members tab state
    const [members, setMembers] = useState<SettingsMember[]>(MOCK_SETTINGS.members);
    const [invitations, setInvitations] = useState<WorkspaceInvitationDTO[]>([]);
    const [inviteEmail, setInviteEmail] = useState("");
    const [generatedInvite, setGeneratedInvite] = useState<WorkspaceInvitationDTO | null>(null);
    const [actionError, setActionError] = useState("");
    const [actionLoading, setActionLoading] = useState(false);

    const settings = {
        ...MOCK_SETTINGS,
        profile: {
            ...MOCK_SETTINGS.profile,
            name: profile?.name ?? (userName || MOCK_SETTINGS.profile.name),
            email: profile?.email ?? MOCK_SETTINGS.profile.email,
            profileImageUri: profile?.profileImageUri ?? MOCK_SETTINGS.profile.profileImageUri,
        },
        workspace: {
            name: currentWorkspace?.name ?? MOCK_SETTINGS.workspace.name,
        },
    };

    // 현재 사용자가 OWNER인지 멤버 목록에서 판단
    const isOwner = members.some(
        (m) => m.memberId === profile?.id && m.authority === "OWNER"
    );

    useEffect(() => {
        if (!currentWorkspace?.id) return;

        const load = async () => {
            try {
                const membersRes = await getWorkspaceMembers(currentWorkspace.id);
                const memberList = membersRes.data.length
                    ? toSettingsMembers(membersRes.data)
                    : MOCK_SETTINGS.members;
                setMembers(memberList);

                const currentMember = membersRes.data.find((m) => m.memberId === profile?.id);
                if (currentMember?.authority === "OWNER") {
                    const invRes = await getInvitations(currentWorkspace.id);
                    setInvitations(invRes.data);
                }
            } catch {
                setMembers(MOCK_SETTINGS.members);
            }
        };

        load();
    }, [currentWorkspace?.id, profile?.id]);

    const handleEmailInvite = async () => {
        if (!currentWorkspace?.id || !inviteEmail.trim()) return;
        setActionError("");
        setActionLoading(true);
        try {
            const res = await createInvitation(currentWorkspace.id, {email: inviteEmail.trim()});
            setGeneratedInvite(res.data);
            setInviteEmail("");
            // 목록 갱신
            const listRes = await getInvitations(currentWorkspace.id);
            setInvitations(listRes.data);
        } catch (err) {
            setActionError(toFriendlyError(err));
        } finally {
            setActionLoading(false);
        }
    };

    const handleOpenLinkInvite = async () => {
        if (!currentWorkspace?.id) return;
        setActionError("");
        setActionLoading(true);
        try {
            const res = await createInvitation(currentWorkspace.id, {});
            setGeneratedInvite(res.data);
            const listRes = await getInvitations(currentWorkspace.id);
            setInvitations(listRes.data);
        } catch (err) {
            setActionError(toFriendlyError(err));
        } finally {
            setActionLoading(false);
        }
    };

    const handleCancelInvitation = async (invitationId: string) => {
        if (!currentWorkspace?.id) return;
        setActionError("");
        try {
            await cancelInvitation(currentWorkspace.id, invitationId);
            setInvitations((prev) => prev.filter((inv) => inv.invitationId !== invitationId));
        } catch (err) {
            setActionError(toFriendlyError(err));
        }
    };

    const handleRemoveMember = async (memberId: string) => {
        if (!currentWorkspace?.id) return;
        setActionError("");
        try {
            await removeMember(currentWorkspace.id, memberId);
            setMembers((prev) => prev.filter((m) => m.memberId !== memberId));
        } catch (err) {
            setActionError(toFriendlyError(err));
        }
    };

    const handleToggleAuthority = async (memberId: string, currentAuthority: string) => {
        if (!currentWorkspace?.id) return;
        const nextAuthority = currentAuthority === "OWNER" ? "MEMBER" : "OWNER";
        setActionError("");
        try {
            await updateMemberAuthority(currentWorkspace.id, memberId, {authority: nextAuthority});
            setMembers((prev) =>
                prev.map((m) =>
                    m.memberId === memberId ? {...m, authority: nextAuthority} : m
                )
            );
        } catch (err) {
            setActionError(toFriendlyError(err));
        }
    };

    const copyToClipboard = (text: string) => {
        navigator.clipboard.writeText(text).catch(() => {});
    };

    return (
        <div className="flex h-full min-h-0 flex-1 flex-col overflow-hidden">
            <header
                className="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-4 lg:px-8">
                <nav className="hidden gap-6 text-sm font-medium md:flex">
                    <a
                        href="#basic"
                        onClick={(event) => {
                            event.preventDefault();
                            setTab("basic");
                        }}
                        className={tab === "basic" ? "text-primary" : "text-muted-foreground hover:text-primary"}
                    >
                        기본설정
                    </a>
                    <a
                        href="#members"
                        onClick={(event) => {
                            event.preventDefault();
                            setTab("members");
                        }}
                        className={tab === "members" ? "text-primary" : "text-muted-foreground hover:text-primary"}
                    >
                        멤버 관리
                    </a>
                </nav>
            </header>
            <main className="flex-1 overflow-y-auto bg-background">
                <div className="mx-auto max-w-5xl px-4 py-6 lg:px-6 lg:py-8">
                    {tab === "basic" ? (
                        <div className="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
                            <Card>
                                <CardContent className="space-y-5">
                                    <div className="flex items-center gap-2">
                                        <UserRound size={18} className="text-primary"/>
                                        <h2 className="text-base font-semibold">프로필 설정</h2>
                                    </div>
                                    <div className="flex flex-col gap-5 sm:flex-row">
                                        <div className="flex w-32 flex-col items-center gap-2">
                                            <Avatar className="size-24">
                                                <AvatarImage src={settings.profile.profileImageUri ?? undefined}
                                                             alt={settings.profile.name}/>
                                                <AvatarFallback
                                                    className="bg-secondary text-xl font-bold text-secondary-foreground">
                                                    {settings.profile.name.charAt(0)}
                                                </AvatarFallback>
                                            </Avatar>
                                            <Button type="button" variant="outline" size="sm">
                                                <Camera size={14}/>
                                                이미지 변경
                                            </Button>
                                        </div>
                                        <div className="grid flex-1 gap-4 sm:grid-cols-2">
                                            <div className="space-y-1.5">
                                                <Label htmlFor="name">이름</Label>
                                                <Input key={settings.profile.name} id="name" defaultValue={settings.profile.name} placeholder="이름"/>
                                            </div>
                                            <div className="space-y-1.5">
                                                <Label htmlFor="email">이메일</Label>
                                                <Input key={settings.profile.email} id="email" type="email" defaultValue={settings.profile.email}
                                                       placeholder="email@kinfolk.com"/>
                                            </div>
                                            <div className="space-y-1.5 sm:col-span-2">
                                                <Label htmlFor="workspace">워크스페이스</Label>
                                                <Input key={settings.workspace.name} id="workspace" defaultValue={settings.workspace.name}
                                                       placeholder="우리 가족"/>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardContent className="space-y-5">
                                    <div className="flex items-center gap-2">
                                        <Palette size={18} className="text-primary"/>
                                        <h2 className="text-base font-semibold">테마 설정</h2>
                                    </div>
                                    <div className="flex items-center justify-between rounded-lg bg-muted/60 p-3">
                                        <div>
                                            <p className="text-sm font-medium">다크 모드</p>
                                            <p className="text-xs text-muted-foreground">어두운 환경용 화면</p>
                                        </div>
                                        <Switch aria-label="다크 모드"/>
                                    </div>
                                    <div className="grid grid-cols-4 gap-3">
                                        {settings.themes.map((theme) => (
                                            <button key={theme.name} type="button"
                                                    className="flex flex-col items-center gap-2 text-xs text-muted-foreground">
                        <span
                            className={cn("size-10 rounded-full border border-border", theme.selected && "ring-2 ring-primary ring-offset-2")}
                            style={{backgroundColor: theme.color}}
                        />
                                                {theme.name}
                                            </button>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>

                            <Card className="lg:col-span-2">
                                <CardContent className="space-y-1">
                                    <div className="mb-3 flex items-center gap-2">
                                        <Bell size={18} className="text-primary"/>
                                        <h2 className="text-base font-semibold">알림 설정</h2>
                                    </div>
                                    {settings.notifications.map((notification) => (
                                        <div key={notification.id}
                                             className="flex items-center justify-between gap-4 border-t border-border py-3 first:border-t-0">
                                            <div>
                                                <p className="text-sm font-medium">{notification.title}</p>
                                                <p className="text-xs text-muted-foreground">{notification.description}</p>
                                            </div>
                                            <Switch defaultChecked={notification.enabled} aria-label={notification.title}/>
                                        </div>
                                    ))}
                                </CardContent>
                            </Card>
                        </div>
                    ) : (
                        <div className="space-y-4">
                            {/* 에러 메시지 */}
                            {actionError && (
                                <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2.5 flex items-center justify-between">
                                    <p className="text-xs text-destructive">{actionError}</p>
                                    <button type="button" onClick={() => setActionError("")} className="text-destructive/60 hover:text-destructive">
                                        <X size={14}/>
                                    </button>
                                </div>
                            )}

                            {/* 초대 섹션 (OWNER만) */}
                            {isOwner && (
                                <>
                                    <section className="grid gap-4 lg:grid-cols-[1fr_1fr]">
                                        <Card>
                                            <CardContent className="space-y-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="flex size-10 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                                                        <Mail size={18}/>
                                                    </div>
                                                    <div>
                                                        <h2 className="text-base font-semibold">이메일로 초대</h2>
                                                        <p className="text-xs text-muted-foreground">초대 링크를 생성합니다.</p>
                                                    </div>
                                                </div>
                                                <div className="flex gap-2">
                                                    <Input
                                                        type="email"
                                                        placeholder="email@family.com"
                                                        value={inviteEmail}
                                                        onChange={(e) => setInviteEmail(e.target.value)}
                                                        onKeyDown={(e) => e.key === "Enter" && handleEmailInvite()}
                                                        disabled={actionLoading}
                                                    />
                                                    <Button
                                                        type="button"
                                                        onClick={handleEmailInvite}
                                                        disabled={actionLoading || !inviteEmail.trim()}
                                                    >
                                                        생성
                                                    </Button>
                                                </div>
                                                <p className="text-xs text-muted-foreground">
                                                    ※ 현재 메일 발송이 준비 중입니다. 생성된 링크를 직접 공유해 주세요.
                                                </p>
                                            </CardContent>
                                        </Card>

                                        <Card>
                                            <CardContent className="space-y-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="flex size-10 items-center justify-center rounded-lg bg-secondary text-secondary-foreground">
                                                        <Link2 size={18}/>
                                                    </div>
                                                    <div>
                                                        <h2 className="text-base font-semibold">링크로 초대</h2>
                                                        <p className="text-xs text-muted-foreground">3일간 유효한 참여 링크입니다.</p>
                                                    </div>
                                                </div>
                                                <Button
                                                    type="button"
                                                    variant="outline"
                                                    className="w-full"
                                                    onClick={handleOpenLinkInvite}
                                                    disabled={actionLoading}
                                                >
                                                    초대 링크 생성
                                                </Button>
                                            </CardContent>
                                        </Card>
                                    </section>

                                    {/* 생성된 초대 링크 */}
                                    {generatedInvite && (
                                        <Card>
                                            <CardContent className="space-y-2">
                                                <p className="text-sm font-medium">초대 링크가 생성되었습니다</p>
                                                <p className="text-xs text-muted-foreground">아래 링크를 초대할 사람에게 공유해 주세요.</p>
                                                <div className="flex items-center gap-2 rounded-lg border border-border bg-muted/60 p-2">
                                                    <span className="flex-1 truncate text-sm text-muted-foreground">
                                                        {generatedInvite.inviteUrl}
                                                    </span>
                                                    <Button
                                                        type="button"
                                                        variant="outline"
                                                        size="icon"
                                                        aria-label="초대 링크 복사"
                                                        onClick={() => copyToClipboard(generatedInvite.inviteUrl)}
                                                    >
                                                        <Copy size={14}/>
                                                    </Button>
                                                </div>
                                            </CardContent>
                                        </Card>
                                    )}

                                    {/* 초대 목록 */}
                                    {invitations.length > 0 && (
                                        <Card>
                                            <CardContent>
                                                <h2 className="mb-3 text-base font-semibold">초대 내역</h2>
                                                <div className="space-y-2">
                                                    {invitations.map((inv) => (
                                                        <div
                                                            key={inv.invitationId}
                                                            className="flex items-center justify-between gap-3 rounded-lg border border-border p-3"
                                                        >
                                                            <div className="min-w-0 flex-1">
                                                                <p className="truncate text-sm font-medium">
                                                                    {inv.inviteEmail ?? "오픈링크"}
                                                                </p>
                                                                <p className="text-xs text-muted-foreground">
                                                                    {inv.expireDt
                                                                        ? `만료: ${new Date(inv.expireDt).toLocaleDateString("ko-KR")}`
                                                                        : ""}
                                                                </p>
                                                            </div>
                                                            <div className="flex items-center gap-2">
                                                                <Badge variant={STATUS_VARIANT[inv.status]}>
                                                                    {STATUS_LABEL[inv.status]}
                                                                </Badge>
                                                                {inv.status === "PENDING" && (
                                                                    <Button
                                                                        type="button"
                                                                        variant="ghost"
                                                                        size="icon-sm"
                                                                        aria-label="초대 취소"
                                                                        onClick={() => handleCancelInvitation(inv.invitationId)}
                                                                    >
                                                                        <X size={14}/>
                                                                    </Button>
                                                                )}
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            </CardContent>
                                        </Card>
                                    )}
                                </>
                            )}

                            {/* 멤버 목록 */}
                            <Card>
                                <CardContent>
                                    <div className="mb-4 flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <Users size={18} className="text-primary"/>
                                            <h2 className="text-base font-semibold">가족 멤버</h2>
                                        </div>
                                        <span className="text-xs text-muted-foreground">{members.length}명</span>
                                    </div>
                                    <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                                        {members.map((member) => (
                                            <div
                                                key={member.memberId}
                                                className="rounded-lg border border-border bg-card p-4"
                                            >
                                                <div className="flex items-center gap-3">
                                                    <Avatar size="lg">
                                                        <AvatarImage src={member.profileImageUri ?? undefined} alt={member.name}/>
                                                        <AvatarFallback className="font-semibold">
                                                            {member.name.charAt(0)}
                                                        </AvatarFallback>
                                                    </Avatar>
                                                    <div className="min-w-0 flex-1">
                                                        <p className="truncate text-sm font-semibold">{member.name}</p>
                                                        <p className="text-xs text-muted-foreground">
                                                            {member.authority === "OWNER" ? "관리자" : "멤버"}
                                                        </p>
                                                    </div>
                                                    {member.authority === "OWNER" && (
                                                        <Shield size={16} className="text-primary shrink-0"/>
                                                    )}
                                                </div>
                                                <div className="mt-4 h-2 overflow-hidden rounded-full bg-muted">
                                                    <div
                                                        className="h-full rounded-full bg-primary"
                                                        style={{width: `${member.participationRate}%`}}
                                                    />
                                                </div>
                                                <p className="mt-2 text-xs text-muted-foreground">주간 참여도</p>

                                                {/* OWNER 전용 액션 (본인 제외) */}
                                                {isOwner && member.memberId !== profile?.id && (
                                                    <div className="mt-3 flex gap-2 border-t border-border pt-3">
                                                        <Button
                                                            type="button"
                                                            variant="outline"
                                                            size="xs"
                                                            className="flex-1"
                                                            onClick={() => handleToggleAuthority(member.memberId, member.authority)}
                                                        >
                                                            {member.authority === "OWNER" ? "멤버로 변경" : "관리자로 변경"}
                                                        </Button>
                                                        <Button
                                                            type="button"
                                                            variant="destructive"
                                                            size="xs"
                                                            onClick={() => handleRemoveMember(member.memberId)}
                                                        >
                                                            내보내기
                                                        </Button>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    )}

                    <div className="mt-5 flex justify-end gap-2">
                        <Button type="button" variant="outline">취소</Button>
                        <Button type="button">저장하기</Button>
                    </div>
                </div>
            </main>
        </div>
    );
}
