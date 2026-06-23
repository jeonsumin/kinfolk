"use client";

import {useState, useEffect, useMemo, type FormEvent} from "react";
import {ChevronLeft, ChevronRight, Plus, Sparkles, MapPin, Check} from "lucide-react";
import {TopBar} from "@/shared/ui/top-bar";
import {
    Avatar,
    AvatarFallback,
    Button,
    Checkbox,
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    Input,
    Label,
    Switch,
} from "@/shared/ui";
import {cn} from "@/shared/utils";
import {
    getMonthEvents,
    getWeekEvents,
    getDayEvents,
    createCalendarEvent,
    getWorkspaceMembers,
    type CalendarEventDTO,
    type EventColor,
    type CreateEventPayload,
    type CalendarCreateRequest,
    type WorkspaceMemberDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";
import {ApiError} from "@/shared/api";

/* ── View modes ── */
const VIEW_MODES = [
    {id: "month", label: "월"},
    {id: "week", label: "주"},
    {id: "day", label: "일"},
] as const;
type ViewMode = (typeof VIEW_MODES)[number]["id"];

/* ── UI-level event types (mapped from DTO) ── */
type CalendarEvent = { id: string; day: number; label: string; color: EventColor };
type TimedEvent = {
    id: string;
    dayIndex: number; // 0=일 ~ 6=토
    start: number;    // 소수 시각 (14.5 = 14:30)
    end: number;
    label: string;
    color: EventColor;
};

/* ── 멤버 컬러 팔레트 (인덱스 순환) ── */
const MEMBER_PALETTE = [
    {rowBg: "bg-[#d2e1f7]/40 border-[#516072]/10", avatarBg: "bg-[#d2e1f7] text-[#516072]"},
    {rowBg: "bg-[#ead6f0]/30 border-[#7c4d8a]/10", avatarBg: "bg-[#ead6f0] text-[#7c4d8a]"},
    {rowBg: "bg-[#d1f5e4]/50 border-[#2e7d5a]/10", avatarBg: "bg-[#d1f5e4] text-[#2e7d5a]"},
    {rowBg: "bg-[#fdf0d0]/40 border-[#8a6800]/10", avatarBg: "bg-[#fdf0d0] text-[#8a6800]"},
];

function getMemberStyle(idx: number) {
    return MEMBER_PALETTE[idx % MEMBER_PALETTE.length];
}

/* ── Styles ── */
const DAY_LABELS = ["일", "월", "화", "수", "목", "금", "토"];
const DOW_KR = ["일", "월", "화", "수", "목", "금", "토"];

const EVENT_STYLES: Record<EventColor, string> = {
    mauve: "bg-[#ead6f0]/50 text-[#7c4d8a] border-[#7c4d8a]",
    blue: "bg-[#d2e1f7]/60 text-[#516072] border-[#516072]",
    green: "bg-[#d1f5e4]/70 text-[#2e7d5a] border-[#2e7d5a]",
    neutral: "bg-muted text-muted-foreground border-border",
};

/* ── Time grid constants ── */
const START_HOUR = 6;
const END_HOUR = 22;
const HOUR_HEIGHT = 56;
const HOURS = Array.from({length: END_HOUR - START_HOUR + 1}, (_, i) => START_HOUR + i);
const GRID_HEIGHT = (END_HOUR - START_HOUR) * HOUR_HEIGHT;

/* ── Date helpers ── */
function fmt(d: Date) {
    return d.toISOString().slice(0, 10);
}

function fmtTime(h: number) {
    const hh = Math.floor(h);
    const mm = Math.round((h - hh) * 60);
    return `${String(hh).padStart(2, "0")}:${String(mm).padStart(2, "0")}`;
}

function pad2(n: number) {
    return String(n).padStart(2, "0");
}

/** 해당 주의 일요일 00:00 반환 */
function getWeekSunday(date: Date): Date {
    const d = new Date(date);
    d.setDate(d.getDate() - d.getDay());
    d.setHours(0, 0, 0, 0);
    return d;
}

/** 뷰에 따라 currentDate를 direction(±1)만큼 이동 */
function shiftDate(view: ViewMode, date: Date, direction: -1 | 1): Date {
    const d = new Date(date);
    if (view === "month") d.setMonth(d.getMonth() + direction);
    else if (view === "week") d.setDate(d.getDate() + direction * 7);
    else d.setDate(d.getDate() + direction);
    return d;
}

/** 주간 헤더에 표시할 7일 배열 생성 */
function buildWeekDays(sunday: Date, today: Date) {
    return Array.from({length: 7}, (_, i) => {
        const d = new Date(sunday);
        d.setDate(d.getDate() + i);
        return {
            dow: DOW_KR[i],
            date: d.getDate(),
            isToday: d.toDateString() === today.toDateString(),
        };
    });
}

/** 월 그리드 셀 생성 */
function buildCalendarCells(year: number, month: number) {
    const firstDow = new Date(year, month - 1, 1).getDay();
    const daysInMonth = new Date(year, month, 0).getDate();
    const daysInPrev = new Date(year, month - 1, 0).getDate();
    const cells: { day: number; currentMonth: boolean }[] = [];
    for (let i = firstDow - 1; i >= 0; i--) cells.push({day: daysInPrev - i, currentMonth: false});
    for (let i = 1; i <= daysInMonth; i++) cells.push({day: i, currentMonth: true});
    const total = Math.ceil(cells.length / 7) * 7;
    let next = 1;
    while (cells.length < total) cells.push({day: next++, currentMonth: false});
    return cells;
}

/* ── DTO → UI 매핑 ── */
function dtoToCalendarEvent(dto: CalendarEventDTO): CalendarEvent {
    const d = new Date(dto.startDt);
    return {id: dto.id, day: d.getDate(), label: dto.title, color: dto.color};
}

function dtoToTimedEvent(dto: CalendarEventDTO, weekSunday: Date): TimedEvent | null {
    if (dto.isAllDay) return null;
    const start = new Date(dto.startDt);
    const end = new Date(dto.endDt);
    const base = new Date(weekSunday);
    base.setHours(0, 0, 0, 0);
    const dayIndex = Math.round((new Date(start.toDateString()).getTime() - base.getTime()) / 86400000);
    if (dayIndex < 0 || dayIndex > 6) return null;
    return {
        id: dto.id,
        dayIndex,
        start: start.getHours() + start.getMinutes() / 60,
        end: end.getHours() + end.getMinutes() / 60,
        label: dto.title,
        color: dto.color,
    };
}

/* ── 뷰 타이틀 ── */
function getViewTitle(view: ViewMode, date: Date): { title: string; subtitle: (count: number) => string } {
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const sunday = getWeekSunday(date);
    const saturday = new Date(sunday);
    saturday.setDate(saturday.getDate() + 6);
    switch (view) {
        case "month":
            return {
                title: `${y}년 ${m}월`,
                subtitle: n => `이번 달 ${n}개의 가족 일정`,
            };
        case "week":
            return {
                title: `${m}월 ${sunday.getDate()}일 – ${saturday.getDate()}일`,
                subtitle: n => `이번 주 ${n}개의 가족 일정`,
            };
        case "day":
            return {
                title: `${m}월 ${d}일 ${DOW_KR[date.getDay()]}요일`,
                subtitle: n => `오늘 ${n}개의 가족 일정`,
            };
    }
}

/* ── 서브 컴포넌트 ── */
function EventBlock({event}: { event: TimedEvent }) {
    return (
        <div
            className={cn(
                "absolute left-1 right-1 rounded-md border-l-2 px-2 py-1 overflow-hidden",
                EVENT_STYLES[event.color]
            )}
            style={{
                top: (event.start - START_HOUR) * HOUR_HEIGHT,
                height: (event.end - event.start) * HOUR_HEIGHT - 4,
            }}
        >
            <p className="text-[11px] font-semibold truncate leading-tight">{event.label}</p>
            <p className="text-[10px] opacity-70 truncate">
                {fmtTime(event.start)}–{fmtTime(event.end)}
            </p>
        </div>
    );
}

function TimeGutter() {
    return (
        <div className="w-14 shrink-0">
            {HOURS.slice(0, -1).map((h) => (
                <div key={h} className="relative border-b border-border/30" style={{height: HOUR_HEIGHT}}>
          <span className="absolute -top-2 right-2 text-[10px] font-medium text-muted-foreground tabular-nums">
            {fmtTime(h)}
          </span>
                </div>
            ))}
        </div>
    );
}

/* ── 월 뷰 ── */
function MonthView({
                       year, month, todayDate, events,
                   }: {
    year: number; month: number; todayDate: Date; events: CalendarEvent[];
}) {
    const cells = buildCalendarCells(year, month);
    return (
        <div className="bg-card rounded-xl overflow-hidden border border-border shadow-sm">
            <div className="grid grid-cols-7 bg-muted/60 border-b border-border">
                {DAY_LABELS.map((label) => (
                    <div key={label}
                         className="py-3 text-center text-[10px] font-bold uppercase tracking-widest text-muted-foreground">
                        {label}
                    </div>
                ))}
            </div>
            <div
                className="grid grid-cols-7 [grid-auto-rows:minmax(120px,1fr)] max-md:[grid-auto-rows:minmax(80px,auto)]">
                {cells.map((cell, idx) => {
                    const dayEvents = cell.currentMonth ? events.filter((e) => e.day === cell.day) : [];
                    const isToday =
                        cell.currentMonth &&
                        cell.day === todayDate.getDate() &&
                        month === todayDate.getMonth() + 1 &&
                        year === todayDate.getFullYear();
                    return (
                        <div
                            key={idx}
                            className={cn(
                                "border-r border-b border-border/40 p-3 transition-colors",
                                cell.currentMonth ? "hover:bg-muted/50 cursor-pointer" : "bg-muted/30 opacity-40",
                                isToday && "bg-primary/5"
                            )}
                        >
              <span
                  className={cn("text-sm", cell.currentMonth ? "font-bold" : "font-medium", isToday && "text-primary")}>
                {cell.day}
              </span>
                            {dayEvents.length > 0 && (
                                <div className="mt-2 space-y-1">
                                    {dayEvents.map((event) => (
                                        <div key={event.id}
                                             className={cn("text-[10px] px-2 py-1 rounded border-l-2 truncate font-medium", EVENT_STYLES[event.color])}>
                                            {event.label}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

/* ── 주간 뷰 ── */
function WeekView({
                      weekDays, events,
                  }: {
    weekDays: ReturnType<typeof buildWeekDays>;
    events: TimedEvent[];
}) {
    return (
        <div className="bg-card rounded-xl overflow-hidden border border-border shadow-sm">
            <div className="flex border-b border-border bg-muted/40">
                <div className="w-14 shrink-0"/>
                {weekDays.map((d) => (
                    <div key={d.date} className="flex-1 py-2.5 flex flex-col items-center gap-1">
                        <span
                            className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">{d.dow}</span>
                        <span className={cn(
                            "text-sm font-bold w-7 h-7 flex items-center justify-center rounded-full",
                            d.isToday ? "bg-primary text-primary-foreground" : "text-foreground"
                        )}>
              {d.date}
            </span>
                    </div>
                ))}
            </div>
            <div className="flex">
                <TimeGutter/>
                <div className="flex-1 grid grid-cols-7">
                    {weekDays.map((d, di) => (
                        <div
                            key={d.date}
                            className={cn("relative border-l border-border/40", d.isToday && "bg-primary/5")}
                            style={{height: GRID_HEIGHT}}
                        >
                            {HOURS.slice(0, -1).map((h) => (
                                <div key={h} className="border-b border-border/30" style={{height: HOUR_HEIGHT}}/>
                            ))}
                            {events.filter((e) => e.dayIndex === di).map((event) => (
                                <EventBlock key={event.id} event={event}/>
                            ))}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

/* ── 일간 뷰 ── */
function DayView({date, events}: { date: Date; events: TimedEvent[] }) {
    return (
        <div className="bg-card rounded-xl overflow-hidden border border-border shadow-sm">
            <div className="flex items-center justify-between border-b border-border bg-muted/40 px-5 py-3">
                <div className="flex items-center gap-3">
                  <span
                      className="text-sm font-bold w-9 h-9 flex items-center justify-center rounded-full bg-primary text-primary-foreground">
                    {date.getDate()}
                  </span>
                    <div>
                        <p className="text-sm font-bold text-foreground">{DOW_KR[date.getDay()]}요일</p>
                        <p className="text-xs text-muted-foreground">{events.length}개의 일정</p>
                    </div>
                </div>
            </div>
            <div className='h-2 border-t border-b mb-3'></div>
            <div className="flex">
                <TimeGutter/>
                <div className="relative flex-1 border-l border-border/40" style={{height: GRID_HEIGHT}}>
                    {HOURS.slice(0, -1).map((h) => (
                        <div key={h} className="border-b border-border/30" style={{height: HOUR_HEIGHT}}/>
                    ))}
                    {events.map((event) => (
                        <EventBlock key={event.id} event={event}/>
                    ))}
                </div>
            </div>
        </div>
    );
}

/* ── 일정 추가 모달 ── */
const COLOR_OPTIONS: { value: EventColor; label: string; swatch: string; ring: string }[] = [
    {value: "mauve", label: "라벤더", swatch: "bg-[#ead6f0]", ring: "ring-[#7c4d8a]"},
    {value: "blue", label: "블루", swatch: "bg-[#d2e1f7]", ring: "ring-[#516072]"},
    {value: "green", label: "그린", swatch: "bg-[#d1f5e4]", ring: "ring-[#2e7d5a]"},
    {value: "neutral", label: "그레이", swatch: "bg-muted", ring: "ring-foreground"},
];

function AddEventDialog({
                            open,
                            onOpenChange,
                            onSuccess,
                            wsId,
                            defaultDate,
                            members,
                        }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSuccess: () => void;
    wsId: string;
    defaultDate: Date;
    members: WorkspaceMemberDTO[];
}) {
    const defaultDateStr = `${defaultDate.getFullYear()}-${pad2(defaultDate.getMonth() + 1)}-${pad2(defaultDate.getDate())}`;

    const [title, setTitle] = useState("");
    const [allDay, setAllDay] = useState(false);
    const [startDate, setStartDate] = useState(defaultDateStr);
    const [startTime, setStartTime] = useState("09:00");
    const [endDate, setEndDate] = useState(defaultDateStr);
    const [endTime, setEndTime] = useState("10:00");
    const [location, setLocation] = useState("");
    const [attendees, setAttendees] = useState<string[]>([]);
    const [color, setColor] = useState<EventColor>("blue");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");

    const canSubmit = title.trim().length > 0 && !isSubmitting;

    const reset = () => {
        setTitle("");
        setAllDay(false);
        setStartDate(defaultDateStr);
        setStartTime("09:00");
        setEndDate(defaultDateStr);
        setEndTime("10:00");
        setLocation("");
        setAttendees([]);
        setColor("blue");
        setError("");
    };

    const toggleAttendee = (id: string) =>
        setAttendees((prev) => prev.includes(id) ? prev.filter((a) => a !== id) : [...prev, id]);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!canSubmit) return;
        setError("");
        setIsSubmitting(true);
        try {
            const eventBody: CreateEventPayload = {
                wsId,
                title: title.trim(),
                isAllDay: allDay ? 1 : 0,
                startDt: allDay ? startDate : `${startDate}T${startTime}:00`,
                endDt: allDay ? endDate : `${endDate}T${endTime}:00`,
                location: location.trim() || undefined,
                color,
            };
            const request: CalendarCreateRequest = {
                event: eventBody,
                inviteeUserIds: attendees.length > 0 ? attendees : undefined,
            };
            await createCalendarEvent(request);
            reset();
            onOpenChange(false);
            onSuccess();
        } catch (err) {
            setError(err instanceof ApiError ? err.messages : "일정 저장에 실패했습니다.");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={(next) => {
            if (!next) reset();
            onOpenChange(next);
        }}>
            <DialogContent className="max-w-lg">
                <DialogHeader>
                    <DialogTitle>일정 추가</DialogTitle>
                    <DialogDescription>가족 캘린더에 새 일정을 추가하세요.</DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                        <div className="rounded-lg bg-destructive/10 border border-destructive/20 px-3 py-2">
                            <p className="text-xs text-destructive">{error}</p>
                        </div>
                    )}

                    {/* 제목 */}
                    <div className="space-y-2">
                        <Label htmlFor="event-title">제목</Label>
                        <Input id="event-title" value={title} onChange={(e) => setTitle(e.target.value)}
                               placeholder="예: 가족 회의" autoFocus/>
                    </div>

                    {/* 하루 종일 */}
                    <div className="flex items-center justify-between rounded-lg border border-border px-3 py-2.5">
                        <Label htmlFor="event-allday" className="cursor-pointer">하루 종일</Label>
                        <Switch id="event-allday" checked={allDay} onCheckedChange={setAllDay}/>
                    </div>

                    {/* 시작 */}
                    <div className="space-y-2">
                        <Label>시작</Label>
                        <div className="flex gap-2">
                            <Input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)}
                                   className="flex-1"/>
                            {!allDay && (
                                <Input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)}
                                       className="w-32"/>
                            )}
                        </div>
                    </div>

                    {/* 종료 */}
                    <div className="space-y-2">
                        <Label>종료</Label>
                        <div className="flex gap-2">
                            <Input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)}
                                   className="flex-1"/>
                            {!allDay && (
                                <Input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)}
                                       className="w-32"/>
                            )}
                        </div>
                    </div>

                    {/* 장소 */}
                    <div className="space-y-2">
                        <Label htmlFor="event-location">장소</Label>
                        <div className="relative">
                            <MapPin size={16}
                                    className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"/>
                            <Input id="event-location" value={location} onChange={(e) => setLocation(e.target.value)}
                                   placeholder="예: 거실" className="pl-9"/>
                        </div>
                    </div>

                    {/* 참석자 */}
                    {members.length > 0 && (
                        <div className="space-y-2">
                            <Label>참석자</Label>
                            <div className="space-y-1.5">
                                {members.map((member, idx) => {
                                    const {avatarBg} = getMemberStyle(idx);
                                    const checked = attendees.includes(member.memberId);
                                    const initial = member.name.slice(0, 1);
                                    return (
                                        <label key={member.memberId}
                                               className="flex items-center gap-3 rounded-lg px-2 py-1.5 cursor-pointer hover:bg-muted/40 transition-colors">
                                            <Checkbox checked={checked}
                                                      onCheckedChange={() => toggleAttendee(member.memberId)}/>
                                            <Avatar size="sm">
                                                <AvatarFallback
                                                    className={cn("text-[10px] font-semibold", avatarBg)}>
                                                    {initial}
                                                </AvatarFallback>
                                            </Avatar>
                                            <span className="text-sm text-foreground">{member.name}</span>
                                        </label>
                                    );
                                })}
                            </div>
                        </div>
                    )}

                    {/* 색상 */}
                    <div className="space-y-2">
                        <Label>색상</Label>
                        <div className="flex items-center gap-3">
                            {COLOR_OPTIONS.map((option) => (
                                <button
                                    key={option.value}
                                    type="button"
                                    onClick={() => setColor(option.value)}
                                    aria-label={option.label}
                                    aria-pressed={color === option.value}
                                    className={cn(
                                        "size-8 rounded-full flex items-center justify-center transition-all ring-offset-2 ring-offset-card",
                                        option.swatch,
                                        color === option.value ? cn("ring-2", option.ring) : "hover:scale-105"
                                    )}
                                >
                                    {color === option.value &&
                                        <Check size={16} strokeWidth={3} className="text-foreground/70"/>}
                                </button>
                            ))}
                        </div>
                    </div>

                    <DialogFooter>
                        <DialogClose
                            render={<Button type="button" variant="outline" disabled={isSubmitting}>취소</Button>}/>
                        <Button type="submit" disabled={!canSubmit}>
                            {isSubmitting ? "저장 중..." : "일정 추가"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

/* ── 메인 페이지 ── */
export default function CalendarPage() {
    const {currentWorkspace} = useAuthStore();
    const today = useMemo(() => new Date(), []);

    const [view, setView] = useState<ViewMode>("month");
    const [currentDate, setCurrentDate] = useState(() => new Date());
    const [events, setEvents] = useState<CalendarEventDTO[]>([]);
    const [members, setMembers] = useState<WorkspaceMemberDTO[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [addOpen, setAddOpen] = useState(false);
    const [visibleMembers, setVisibleMembers] = useState<Set<string>>(new Set());

    const weekSunday = useMemo(() => getWeekSunday(currentDate), [currentDate]);
    const weekDays = useMemo(() => buildWeekDays(weekSunday, today), [weekSunday, today]);

    /* 워크스페이스 변경 시 멤버 로드 */
    useEffect(() => {
        if (!currentWorkspace) return;
        getWorkspaceMembers(currentWorkspace.id)
            .then((res) => {
                const list = res.data ?? [];
                setMembers(list);
                setVisibleMembers(new Set(list.map((m) => m.memberId)));
            })
            .catch(() => {
                setMembers([]);
            });
    }, [currentWorkspace?.id]);

    /* 뷰/날짜 변경 시 이벤트 로드 */
    useEffect(() => {
        if (!currentWorkspace) return;
        let cancelled = false;

        const load = async () => {
            setIsLoading(true);
            try {
                let res;
                if (view === "month") {
                    res = await getMonthEvents(currentWorkspace.id, currentDate.getFullYear(), currentDate.getMonth() + 1);
                } else if (view === "week") {
                    res = await getWeekEvents(currentWorkspace.id, weekSunday);
                } else {
                    res = await getDayEvents(currentWorkspace.id, currentDate);
                }
                if (!cancelled) setEvents(res.data ?? []);
            } catch {
                if (!cancelled) setEvents([]);
            } finally {
                if (!cancelled) setIsLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [view, currentDate, currentWorkspace?.id, weekSunday]);

    /* DTO → UI 타입 매핑 */
    const monthEvents = useMemo(() => events.map(dtoToCalendarEvent), [events]);
    const timedEvents = useMemo(
        () => events.flatMap((dto) => {
            const t = dtoToTimedEvent(dto, weekSunday);
            return t ? [t] : [];
        }),
        [events, weekSunday]
    );

    /* 가족 필터 — visibleMembers에 포함된 userId의 attendee가 있는 이벤트만 표시 */
    const filteredMonthEvents = useMemo(
        () => visibleMembers.size === 0
            ? monthEvents
            : monthEvents.filter((e) => {
                const dto = events.find((d) => d.id === e.id);
                if (!dto) return true;
                return dto.attendees.some((a) => visibleMembers.has(a.userId));
            }),
        [monthEvents, events, visibleMembers]
    );
    const filteredTimedEvents = useMemo(
        () => visibleMembers.size === 0
            ? timedEvents
            : timedEvents.filter((e) => {
                const dto = events.find((d) => d.id === e.id);
                if (!dto) return true;
                return dto.attendees.some((a) => visibleMembers.has(a.userId));
            }),
        [timedEvents, events, visibleMembers]
    );

    const {title: viewTitle, subtitle: viewSubtitle} = getViewTitle(view, currentDate);

    const handlePrev = () => setCurrentDate((d) => shiftDate(view, d, -1));
    const handleNext = () => setCurrentDate((d) => shiftDate(view, d, 1));
    const handleToday = () => setCurrentDate(new Date());

    const toggleMember = (id: string) =>
        setVisibleMembers((prev) => {
            const next = new Set(prev);
            if (next.has(id)) next.delete(id); else next.add(id);
            return next;
        });

    return (
        <div className="flex flex-col flex-1 h-full overflow-hidden">
            <TopBar/>

            <div className="flex-1 overflow-y-auto">
                <div className="max-w-7xl mx-auto px-4 lg:px-6 py-5 lg:py-6 flex flex-col gap-6 lg:flex-row">

                    {/* ── 캘린더 섹션 ── */}
                    <section className="flex-1 min-w-0 flex flex-col gap-4">
                        {/* Header */}
                        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
                            <div>
                                <div className="flex items-center gap-2">
                                    {/* 날짜 이동 */}
                                    <Button variant="ghost" size="icon-sm" onClick={handlePrev} aria-label="이전">
                                        <ChevronLeft size={16}/>
                                    </Button>
                                    <h2 className="text-2xl font-bold text-foreground tracking-tight leading-tight">
                                        {viewTitle}
                                    </h2>
                                    <Button variant="ghost" size="icon-sm" onClick={handleNext} aria-label="다음">
                                        <ChevronRight size={16}/>
                                    </Button>
                                </div>
                                <p className="text-sm text-muted-foreground mt-0.5">
                                    {isLoading ? "불러오는 중..." : viewSubtitle(events.length)}
                                </p>
                            </div>

                            <div className="flex flex-col items-stretch sm:items-end gap-2">
                                {/* 뷰 토글 */}
                                <div
                                    className="flex items-center bg-muted p-1 rounded-lg border border-border self-start sm:self-end">
                                    <Button
                                        variant="ghost" size="sm"
                                        onClick={handleToday}
                                        className="px-3 text-muted-foreground hover:bg-transparent"
                                    >
                                        오늘
                                    </Button>
                                    {VIEW_MODES.map((mode) => (
                                        <Button
                                            key={mode.id}
                                            variant="ghost" size="sm"
                                            onClick={() => setView(mode.id)}
                                            className={cn(
                                                "px-4",
                                                view === mode.id
                                                    ? "bg-card shadow-sm font-semibold text-foreground hover:bg-card"
                                                    : "text-muted-foreground hover:bg-transparent"
                                            )}
                                        >
                                            {mode.label}
                                        </Button>
                                    ))}
                                </div>

                                {/* 일정 추가 */}
                                {currentWorkspace && (
                                    <Button className="w-full sm:w-auto" onClick={() => setAddOpen(true)}>
                                        <Plus size={18} strokeWidth={2.4}/>
                                        일정 추가
                                    </Button>
                                )}
                            </div>
                        </div>

                        {/* 뷰 */}
                        {view === "month" && (
                            <MonthView
                                year={currentDate.getFullYear()}
                                month={currentDate.getMonth() + 1}
                                todayDate={today}
                                events={filteredMonthEvents}
                            />
                        )}
                        {view === "week" && <WeekView weekDays={weekDays} events={filteredTimedEvents}/>}
                        {view === "day" &&
                            <DayView date={currentDate} events={filteredTimedEvents}/>}
                    </section>

                    {/* ── 필터 패널 ── */}
                    <aside className="lg:w-80 shrink-0 flex flex-col gap-5">
                        <div className="bg-card rounded-xl p-5 border border-border shadow-sm">
                            <h3 className="text-base font-semibold text-foreground mb-4 flex items-center gap-2">
                                <Sparkles size={18} className="text-primary" strokeWidth={1.8}/>
                                가족 보기
                            </h3>

                            <div className="space-y-3">
                                {members.map((member, idx) => {
                                    const {rowBg, avatarBg} = getMemberStyle(idx);
                                    const eventCount = events.filter((e) =>
                                        e.attendees.some((a) => a.userId === member.memberId)
                                    ).length;
                                    const initial = member.name.slice(0, 1);
                                    return (
                                        <div
                                            key={member.memberId}
                                            className={cn(
                                                "flex items-center justify-between p-3 rounded-lg border cursor-pointer transition-colors",
                                                rowBg
                                            )}
                                        >
                                            <div className="flex items-center gap-3">
                                                <Avatar size="lg">
                                                    <AvatarFallback className={cn("font-semibold", avatarBg)}>
                                                        {initial}
                                                    </AvatarFallback>
                                                </Avatar>
                                                <div>
                                                    <p className="font-bold text-foreground text-sm">{member.name}</p>
                                                    <p className="text-xs text-muted-foreground">일정 {eventCount}개</p>
                                                </div>
                                            </div>
                                            <Switch
                                                checked={visibleMembers.has(member.memberId)}
                                                onCheckedChange={() => toggleMember(member.memberId)}
                                            />
                                        </div>
                                    );
                                })}
                                {members.length === 0 && (
                                    <p className="text-xs text-muted-foreground text-center py-2">멤버 정보 없음</p>
                                )}
                            </div>
                        </div>
                    </aside>
                </div>
            </div>

            {/* 일정 추가 모달 */}
            {currentWorkspace && (
                <AddEventDialog
                    open={addOpen}
                    onOpenChange={setAddOpen}
                    onSuccess={() => {
                        setCurrentDate((d) => new Date(d));
                    }}
                    wsId={currentWorkspace.id}
                    defaultDate={currentDate}
                    members={members}
                />
            )}
        </div>
    );
}
