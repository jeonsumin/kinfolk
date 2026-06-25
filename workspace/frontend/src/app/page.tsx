"use client";

import {useEffect, useState} from "react";
import {
    CalendarDays,
    Clock,
    MapPin,
    ShoppingCart,
    Heart,
    Target,
    Send,
    Camera,
    CloudSun,
    Plus,
    Images,
} from "lucide-react";
import {TopBar} from "@/shared/ui/top-bar";
import {
    Avatar,
    AvatarFallback,
    AvatarGroup,
    AvatarGroupCount,
    Button,
    Checkbox,
    Chip,
    ImageOverlayCard,
    Input,
    Progress,
    ProgressLabel,
    ProgressValue,
    Skeleton,
} from "@/shared/ui";
import {
    getWorkspaceMembers,
    getDayEvents,
    getShoppingList,
    getMonthEvents,
    type WorkspaceMemberDTO,
    type CalendarEventDTO,
    type ShoppingItemDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";
import {cn} from "@/shared/utils";

/* ── 상수 ── */
const MEMBER_PALETTE = [
    {bg: "#d2e1f7", text: "#516072"},
    {bg: "#ead6f0", text: "#7c4d8a"},
    {bg: "#d1f5e4", text: "#2e7d5a"},
    {bg: "#fdf0d0", text: "#8a6800"},
];

const EVENT_COLORS: Record<string, { chip: "blue" | "green" | "slate" | "amber"; accent: string }> = {
    blue: {chip: "blue", accent: "#516072"},
    green: {chip: "green", accent: "#2e7d5a"},
    mauve: {chip: "slate", accent: "#475569"},
    neutral: {chip: "amber", accent: "#8a6800"},
};

const DAYS_KO = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];

const QUICK_TAGS = ["🥕 장보기", "🧼 집안일", "🎉 기념일"];

const CHORES = [
    {label: "주방 청소하기", value: 80},
    {label: "뒷마당 가꾸기", value: 35},
];

const PHOTOS = [
    {title: "밀러 호수 소풍", date: "2024.10.20", gradient: "from-[#b9c7df] to-[#475569]"},
    {title: "주말 아침 식사", date: "2024.10.19", gradient: "from-[#d4e4fa] to-[#516072]"},
    {title: "미아의 그림 교실", date: "2024.10.15", gradient: "from-[#bbcae1] to-[#343e47]"},
    {title: "공원 산책", date: "2024.10.12", gradient: "from-[#c4d4ec] to-[#303e51]"},
];

/* ── 헬퍼 ── */
function extractTime(dtStr: string): string {
    const m = dtStr.match(/[T ](\d{2}:\d{2})/);
    return m ? m[1] : "";
}

function isEventNow(event: CalendarEventDTO): boolean {
    const now = Date.now();
    const start = new Date(event.startDt.replace(" ", "T")).getTime();
    const end = new Date(event.endDt.replace(" ", "T")).getTime();
    return start <= now && now <= end;
}

function formatUpcomingMeta(startDt: string): string {
    const eventDate = new Date(startDt.slice(0, 10) + "T00:00:00");
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const diffDays = Math.round((eventDate.getTime() - today.getTime()) / 86400000);
    const month = eventDate.getMonth() + 1;
    const day = eventDate.getDate();
    if (diffDays === 0) return `오늘 • ${month}월 ${day}일`;
    if (diffDays === 1) return `내일 • ${month}월 ${day}일`;
    return `${diffDays}일 남음 • ${month}월 ${day}일`;
}

/* ── Page ── */
export default function DashboardPage() {
    const {currentWorkspace, userName, profile} = useAuthStore();
    const displayName = profile?.name ?? userName;

    // 클라이언트에서만 시간 기반 인사말 계산 (hydration mismatch 방지)
    const [greeting, setGreeting] = useState("안녕하세요");
    const [dayLabel, setDayLabel] = useState("");
    useEffect(() => {
        const now = new Date();
        const hour = now.getHours();
        setGreeting(hour < 12 ? "좋은 아침이에요" : hour < 17 ? "좋은 오후예요" : "좋은 저녁이에요");
        setDayLabel(DAYS_KO[now.getDay()] + "입니다.");
    }, []);

    const [members, setMembers] = useState<WorkspaceMemberDTO[]>([]);
    const [todayEvents, setTodayEvents] = useState<CalendarEventDTO[]>([]);
    const [shoppingItems, setShoppingItems] = useState<ShoppingItemDTO[]>([]);
    const [upcomingEvents, setUpcomingEvents] = useState<CalendarEventDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (!currentWorkspace) {
            setIsLoading(false);
            return;
        }
        const wsId = currentWorkspace.id;
        let cancelled = false;
        setIsLoading(true);

        const today = new Date();
        const year = today.getFullYear();
        const month = today.getMonth() + 1;
        const nextMonth = month === 12 ? 1 : month + 1;
        const nextYear = month === 12 ? year + 1 : year;
        const todayStr = today.toISOString().slice(0, 10);

        Promise.all([
            getWorkspaceMembers(wsId),
            getDayEvents(wsId, today),
            getShoppingList(wsId),
            getMonthEvents(wsId, year, month),
            getMonthEvents(wsId, nextYear, nextMonth),
        ])
            .then(([membersRes, dayRes, shoppingRes, thisMonthRes, nextMonthRes]) => {
                if (cancelled) return;

                setMembers(membersRes.data ?? []);
                setTodayEvents(dayRes.data ?? []);

                const allItems = (shoppingRes.data ?? []).flatMap((cat) => cat.items ?? []);
                const unchecked = allItems.filter((i) => !i.isChecked);
                setShoppingItems(unchecked.slice(0, 3));

                const allMonthEvents = [
                    ...(thisMonthRes.data ?? []),
                    ...(nextMonthRes.data ?? []),
                ];
                const upcoming = allMonthEvents
                    .filter((e) => e.startDt.slice(0, 10) > todayStr)
                    .sort((a, b) => a.startDt.localeCompare(b.startDt))
                    .slice(0, 3);
                setUpcomingEvents(upcoming);
            })
            .catch(() => {
                // 에러 발생 시 빈 상태 유지
            })
            .finally(() => {
                if (!cancelled) setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [currentWorkspace?.id]);

    const visibleMembers = members.slice(0, 4);
    const extraCount = Math.max(0, members.length - 4);

    return (
        <div className="flex flex-col flex-1 h-full overflow-hidden">
            <TopBar/>

            <div className="flex-1 overflow-y-auto">
                <div className="max-w-5xl mx-auto px-4 lg:px-6 py-5 lg:py-6">

                    {/* ── Welcome header ── */}
                    <header className="mb-6 flex flex-col md:flex-row md:items-end justify-between gap-4">
                        <div className="flex-1 flex flex-col md:flex-row md:items-center justify-between gap-4">
                            <div className="space-y-1">
                                <h2 className="text-2xl lg:text-[32px] font-semibold text-foreground tracking-tight leading-tight">
                                    {greeting}{displayName ? `, ${displayName}님!` : "!"}
                                </h2>
                                <p className="text-sm text-muted-foreground">{dayLabel}</p>
                            </div>

                            {/* Weather — D-2-7 정적 유지 */}
                            <div className="flex items-center gap-3">
                                <div className="flex items-center gap-1">
                                    <CloudSun size={26} className="text-primary" strokeWidth={1.8}/>
                                    <span className="text-2xl font-extrabold text-primary">22°C</span>
                                </div>
                                <div className="flex flex-col gap-0.5">
                                    <span className="text-sm font-bold text-foreground leading-tight">맑음</span>
                                    <Chip color="blue" size="sm" className="font-extrabold uppercase tracking-wider">
                                        <span className="w-1.5 h-1.5 rounded-full bg-secondary-foreground"/>
                                        미세먼지 좋음
                                    </Chip>
                                </div>
                            </div>
                        </div>

                        {/* D-2-3 멤버 아바타 */}
                        {isLoading ? (
                            <div className="flex gap-1">
                                {[0, 1, 2].map((i) => (
                                    <Skeleton key={i} className="size-10 rounded-full"/>
                                ))}
                            </div>
                        ) : (
                            <AvatarGroup>
                                {visibleMembers.map((m, idx) => (
                                    <Avatar key={m.memberId} size="lg">
                                        <AvatarFallback
                                            className="text-sm font-semibold"
                                            style={{
                                                backgroundColor: MEMBER_PALETTE[idx % MEMBER_PALETTE.length].bg,
                                                color: MEMBER_PALETTE[idx % MEMBER_PALETTE.length].text,
                                            }}
                                        >
                                            {m.name.charAt(0)}
                                        </AvatarFallback>
                                    </Avatar>
                                ))}
                                {extraCount > 0 && <AvatarGroupCount>+{extraCount}</AvatarGroupCount>}
                            </AvatarGroup>
                        )}
                    </header>

                    {/* ── Bento grid ── */}
                    <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">

                        {/* D-2-4 오늘의 일정 */}
                        <section className="lg:col-span-8 bg-card rounded-xl p-5 lg:p-6 border border-border shadow-sm">
                            <div className="flex items-center justify-between mb-6">
                                <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
                                    <Clock size={18} className="text-muted-foreground" strokeWidth={1.8}/>
                                    오늘의 일정
                                </h3>
                                <button className="text-primary font-bold text-sm hover:underline">
                                    캘린더 보기
                                </button>
                            </div>

                            <div className="relative space-y-5">
                                <div
                                    className="absolute left-6 top-2 bottom-2 w-px border-l border-dashed border-border"/>

                                {isLoading ? (
                                    <div className="space-y-4 ml-16">
                                        {[0, 1, 2].map((i) => (
                                            <Skeleton key={i} className="h-16 rounded-xl"/>
                                        ))}
                                    </div>
                                ) : todayEvents.length === 0 ? (
                                    <p className="text-center text-sm text-muted-foreground py-8 ml-16">
                                        오늘 일정이 없습니다.
                                    </p>
                                ) : (
                                    todayEvents.map((event) => {
                                        const colorInfo = EVENT_COLORS[event.color] ?? EVENT_COLORS.neutral;
                                        const active = isEventNow(event);
                                        const time = event.isAllDay === 1 ? "종일" : extractTime(event.startDt);
                                        return (
                                            <div key={event.id} className="flex gap-4 relative z-10">
                                                <div className="w-12 flex flex-col items-center shrink-0">
                          <span
                              className={cn(
                                  "text-xs font-bold mb-1",
                                  active ? "text-primary" : "text-muted-foreground"
                              )}
                          >
                            {time}
                          </span>
                                                    <span
                                                        className="w-4 h-4 rounded-full ring-4 ring-card"
                                                        style={{backgroundColor: colorInfo.accent}}
                                                    />
                                                </div>

                                                <div
                                                    className={cn(
                                                        "flex-1 rounded-xl p-4 border-l-4 transition-shadow cursor-pointer",
                                                        active ? "bg-card ring-1 ring-border shadow-md" : "bg-muted/50"
                                                    )}
                                                    style={{borderLeftColor: colorInfo.accent}}
                                                >
                                                    <div className="flex justify-between items-start gap-2">
                                                        <div>
                                                            {active && (
                                                                <div className="flex items-center gap-2 mb-1">
                                                                    <span
                                                                        className="w-2 h-2 rounded-full bg-primary animate-pulse"/>
                                                                    <span
                                                                        className="text-[10px] font-bold text-primary uppercase">
                                    지금 진행 중
                                  </span>
                                                                </div>
                                                            )}
                                                            <h4 className="font-bold text-foreground">{event.title}</h4>
                                                            {event.location && (
                                                                <p className="text-sm text-muted-foreground flex items-center gap-1 mt-0.5">
                                                                    <MapPin size={14} strokeWidth={1.8}/>
                                                                    {event.location}
                                                                </p>
                                                            )}
                                                        </div>
                                                        <Chip
                                                            color={colorInfo.chip}
                                                            size="sm"
                                                            className="font-extrabold uppercase tracking-wider shrink-0"
                                                        >
                                                            일정
                                                        </Chip>
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })
                                )}
                            </div>
                        </section>

                        {/* Right widgets */}
                        <div className="lg:col-span-4 flex flex-col gap-5">

                            {/* 빠른 추가 — 정적 유지 */}
                            <section
                                className="bg-primary text-primary-foreground rounded-xl p-5 shadow-sm flex flex-col gap-4">
                                <h3 className="text-lg font-semibold">빠른 추가</h3>
                                <div className="space-y-3">
                                    <div className="relative">
                                        <Input
                                            placeholder="아빠에게 상기시켜야 할 일..."
                                            className="bg-white/10 border-white/10 text-white placeholder:text-white/60 h-12 pr-12"
                                        />
                                        <Button
                                            size="icon-sm"
                                            className="absolute right-2 top-1/2 -translate-y-1/2 bg-white text-primary hover:bg-white/90"
                                            aria-label="추가"
                                        >
                                            <Send size={16} strokeWidth={2}/>
                                        </Button>
                                    </div>
                                    <div className="flex flex-wrap gap-2">
                                        {QUICK_TAGS.map((tag) => (
                                            <button
                                                key={tag}
                                                className="px-3 py-1 bg-white/10 rounded-full text-xs font-bold text-white hover:bg-white/20 transition-colors"
                                            >
                                                {tag}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            </section>

                            {/* D-2-5 장보기 리스트 */}
                            <section className="bg-card rounded-xl p-5 border border-border shadow-sm">
                                <div className="flex items-center gap-2 mb-4">
                                    <ShoppingCart size={18} className="text-muted-foreground" strokeWidth={1.8}/>
                                    <h3 className="font-bold text-foreground">장보기 리스트</h3>
                                </div>
                                <div className="space-y-3">
                                    {isLoading ? (
                                        [0, 1, 2].map((i) => <Skeleton key={i} className="h-5 rounded"/>)
                                    ) : shoppingItems.length === 0 ? (
                                        <p className="text-sm text-muted-foreground text-center py-2">
                                            항목이 없습니다.
                                        </p>
                                    ) : (
                                        shoppingItems.map((item) => (
                                            <label key={item.itemId} className="flex items-center gap-3 cursor-pointer">
                                                <Checkbox
                                                    checked={item.isChecked}
                                                    onCheckedChange={() => {
                                                    }}
                                                />
                                                <span
                                                    className={cn(
                                                        "text-sm text-foreground",
                                                        item.isChecked && "line-through text-muted-foreground"
                                                    )}
                                                >
                          {item.itemNm}
                        </span>
                                            </label>
                                        ))
                                    )}
                                </div>
                                <Button
                                    variant="ghost"
                                    className="w-full mt-4 bg-muted/60 hover:bg-muted text-primary font-bold"
                                >
                                    전체 보기
                                </Button>
                            </section>

                            {/* D-2-6 다가오는 일정 */}
                            <section className="bg-card rounded-xl p-5 border border-border shadow-sm">
                                <div className="flex items-center gap-2 mb-4">
                                    <Heart size={18} className="text-muted-foreground" strokeWidth={1.8}/>
                                    <h3 className="font-bold text-foreground">다가오는 일정</h3>
                                </div>
                                <div className="space-y-4">
                                    {isLoading ? (
                                        [0, 1].map((i) => <Skeleton key={i} className="h-12 rounded"/>)
                                    ) : upcomingEvents.length === 0 ? (
                                        <p className="text-sm text-muted-foreground">다가오는 일정이 없습니다.</p>
                                    ) : (
                                        upcomingEvents.map((event) => (
                                            <div key={event.id}
                                                 className="flex items-center gap-4 group cursor-pointer">
                                                <div
                                                    className="w-12 h-12 bg-muted text-muted-foreground rounded-lg flex items-center justify-center shrink-0 group-hover:bg-primary group-hover:text-primary-foreground transition-colors">
                                                    <CalendarDays size={22} strokeWidth={1.8}/>
                                                </div>
                                                <div>
                                                    <h4 className="font-bold text-sm text-foreground">{event.title}</h4>
                                                    <p className="text-xs text-muted-foreground">
                                                        {formatUpcomingMeta(event.startDt)}
                                                    </p>
                                                </div>
                                            </div>
                                        ))
                                    )}
                                </div>
                                <Button
                                    variant="ghost"
                                    className="w-full mt-6 bg-muted/60 hover:bg-muted text-primary font-bold"
                                >
                                    메모리 박스 열기
                                </Button>
                            </section>

                            {/* 가족 공동 목표 — D-2-7 정적 유지 */}
                            <section className="bg-muted/50 rounded-xl p-5 border border-border">
                                <h3 className="font-bold text-foreground mb-2 flex items-center gap-2">
                                    <Target size={18} className="text-muted-foreground" strokeWidth={1.8}/>
                                    가족 공동 목표
                                </h3>
                                <div className="space-y-4 mt-4">
                                    {/*임시 더미데이터 처리*/}
                                    {CHORES.length < 0 ? CHORES.map((chore) => (
                                        <Progress key={chore.label} value={chore.value}>
                                            <ProgressLabel className="text-xs font-bold text-muted-foreground">
                                                {chore.label}
                                            </ProgressLabel>
                                            <ProgressValue className="text-xs font-bold text-muted-foreground"/>
                                        </Progress>
                                    )) : (<p className="text-sm text-muted-foreground"> 공동 목표가 없습니다.</p>)}
                                </div>
                            </section>
                        </div>
                    </div>

                    {/* 가족 사진 공유 — D-2-7 정적 유지 */}
                    <section className="mt-5 bg-card rounded-xl p-5 lg:p-6 border border-border shadow-sm">
                        <div className="flex items-center justify-between mb-4">
                            <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
                                <Images size={18} className="text-muted-foreground" strokeWidth={1.8}/>
                                가족 사진 공유
                            </h3>
                            <button className="text-primary font-bold text-sm hover:underline">
                                갤러리 전체보기
                            </button>
                        </div>

                        <div
                            className="flex gap-4 overflow-x-auto pb-2 snap-x snap-mandatory [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
                            {PHOTOS.length < 0 ? PHOTOS.map((photo) => (
                                <button
                                    key={photo.title}
                                    className="w-48 shrink-0 snap-start aspect-[3/2] rounded-xl bg-muted border-2 border-dashed border-border flex flex-col items-center justify-center text-muted-foreground hover:bg-accent transition-colors">
                                    <ImageOverlayCard
                                        key={photo.title}
                                        title={photo.title}
                                        subtitle={photo.date}
                                        aspectRatio="aspect-[3/2]"
                                        className="w-72 shrink-0 snap-start"
                                    >
                                        <div className={cn("h-full w-full bg-gradient-to-br", photo.gradient)}/>
                                    </ImageOverlayCard>
                                    <Camera size={32} strokeWidth={1.6} className="mb-2"/>
                                    <span className="text-sm font-bold">사진 추가</span>
                                </button>
                            )) : (<p className="text-sm text-muted-foreground"> 사진이 없습니다.</p>)}
                        </div>
                    </section>
                </div>
            </div>

            {/* FAB */}
            <button
                className="fixed bottom-20 right-5 lg:bottom-8 lg:right-8 w-14 h-14 rounded-xl bg-primary text-primary-foreground flex items-center justify-center shadow-xl hover:bg-primary/90 active:scale-95 transition-all z-40"
                aria-label="추가"
            >
                <Plus size={24} strokeWidth={2.5}/>
            </button>
        </div>
    );
}
