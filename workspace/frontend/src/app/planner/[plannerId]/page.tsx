"use client";

import {useAuthStore} from "@/stores/auth-store";
import {useParams, useRouter} from "next/navigation";
import {
    createPlaceSuggestion,
    createSchedulePoll, createSettlementExpense,
    getPlanner,
    getPlaceSuggestions,
    getSchedulePolls,
    getSchedulePollVoteSummary,
    getSettlementExpenses,
    type PlaceSuggestionDTO,
    type PlannerDTO,
    resolvePlacePreview,
    type SchedulePollDTO,
    type SchedulePollVoteSummaryDTO,
    type SettlementExpenseDTO,
    type SettlementStatus,
    togglePlaceSuggestionVote,
    toggleSchedulePollVote
} from "@/shared/api";
import {type FormEvent, useEffect, useState} from "react";
import Link from "next/link";
import {
    CalendarDays,
    ChevronLeft,
    ChevronRight, ExternalLink, Heart,
    Home,
    Info,
    MapPinned,
    Plus, ReceiptText,
    Route,
    UserPlus,
    WalletCards
} from "lucide-react";
import {
    Avatar,
    AvatarFallback,
    Button,
    Card,
    CardContent,
    CardHeader,
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
    Chip,
    CommCalendar,
    Dialog, DialogClose,
    DialogContent, DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle, Input, Label,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@shared/ui";
import {cn} from "@shared/utils";


const dateInputValue = (date = new Date()) =>
    `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;

const EXPENSE_STATUS: Record<SettlementStatus, { label: string; chip: "blue" | "green" | "default" }> = {
    IN_PROGRESS: {label: "정산 진행중", chip: "blue"},
    PENDING: {label: "미완료", chip: "default"},
    SETTLED: {label: "정산 완료", chip: "green"},
};

const formatCurrency = (amount: number) => `₩${amount.toLocaleString("ko-KR")}`;

function SchedulePollDialog({
                                open,
                                onOpenChange,
                                workspaceId,
                                onCreated,
                            }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    workspaceId: string;
    onCreated: (poll: SchedulePollDTO) => void;
}) {
    const [title, setTitle] = useState("");
    const [startDt, setStartDt] = useState(() => dateInputValue());
    const [endDt, setEndDt] = useState(() => dateInputValue());
    const [isSubmitting, setIsSubmitting] = useState(false);
    const isValidPeriod = startDt <= endDt;

    const reset = () => {
        setTitle("");
        setStartDt(dateInputValue());
        setEndDt(dateInputValue());
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        if (!title.trim() || !isValidPeriod || isSubmitting) return;
        setIsSubmitting(true);
        try {
            onCreated(await createSchedulePoll({
                workspaceId,
                title: title.trim(),
                candidates: [{startDt, endDt}],
            }));
            reset();
            onOpenChange(false);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={(next) => {
            if (!next) reset();
            onOpenChange(next);
        }}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>희망 일정 등록</DialogTitle>
                    <DialogDescription>등록한 날짜는 워크스페이스 멤버가 익명으로 투표할 수 있습니다.</DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="planner-poll-title">일정 이름</Label>
                        <Input id="planner-poll-title" value={title} onChange={(event) => setTitle(event.target.value)}
                               placeholder="예: 주말 가족 나들이" autoFocus/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="planner-poll-start">희망 시작일</Label>
                        <Input id="planner-poll-start" type="date" min={dateInputValue()} value={startDt}
                               onChange={(event) => setStartDt(event.target.value)}/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="planner-poll-end">희망 종료일</Label>
                        <Input id="planner-poll-end" type="date" min={startDt} value={endDt}
                               onChange={(event) => setEndDt(event.target.value)}/>
                    </div>
                    {!isValidPeriod && <p className="text-xs text-destructive">종료일은 시작일보다 빠를 수 없습니다.</p>}
                    <DialogFooter>
                        <DialogClose
                            render={<Button type="button" variant="outline" disabled={isSubmitting}>취소</Button>}/>
                        <Button type="submit"
                                disabled={!title.trim() || !isValidPeriod || isSubmitting}>{isSubmitting ? "등록 중..." : "등록하기"}</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

function PlaceSuggestionDialog({
                                   open,
                                   onOpenChange,
                                   workspaceId,
                                   onCreated,
                               }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    workspaceId: string;
    onCreated: (place: PlaceSuggestionDTO) => void;
}) {
    const [sourceUrl, setSourceUrl] = useState("");
    const [thumbnailUrl, setThumbnailUrl] = useState("");
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [category, setCategory] = useState("장소");
    const [isResolving, setIsResolving] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const reset = () => {
        setSourceUrl("");
        setThumbnailUrl("");
        setTitle("");
        setDescription("");
        setCategory("장소");
    };

    const handlePreview = async () => {
        if (!sourceUrl || isResolving) return;
        setIsResolving(true);
        try {
            const preview = await resolvePlacePreview(sourceUrl);
            setThumbnailUrl(preview.thumbnailUrl);
            setTitle(preview.title);
            setDescription(preview.description);
            setCategory(preview.category);
        } finally {
            setIsResolving(false);
        }
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        if (!sourceUrl || !thumbnailUrl || !title.trim() || !description.trim() || !category.trim() || isSubmitting) return;
        setIsSubmitting(true);
        try {
            onCreated(await createPlaceSuggestion({
                workspaceId,
                sourceUrl,
                thumbnailUrl,
                title: title.trim(),
                description: description.trim(),
                category: category.trim(),
            }));
            reset();
            onOpenChange(false);
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
                    <DialogTitle>장소 제안하기</DialogTitle>
                    <DialogDescription>공유 링크에서 미리보기를 가져온 뒤 제목, 설명, 카테고리를 확인하세요.</DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="place-url">장소 링크</Label>
                        <div className="flex gap-2">
                            <Input id="place-url" type="url" value={sourceUrl}
                                   onChange={(event) => setSourceUrl(event.target.value)}/>
                            <Button type="button" variant="outline" onClick={handlePreview}
                                    disabled={!sourceUrl || isResolving}>
                                {isResolving ? "가져오는 중..." : "미리보기"}
                            </Button>
                        </div>
                    </div>
                    {thumbnailUrl && <div className="h-28 rounded-lg bg-muted bg-cover bg-center"
                                          style={{backgroundImage: `url(${thumbnailUrl})`}}/>}
                    <div className="space-y-2">
                        <Label htmlFor="place-title">제목</Label>
                        <Input id="place-title" value={title} onChange={(event) => setTitle(event.target.value)}
                               placeholder="장소 이름"/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="place-description">설명</Label>
                        <Input id="place-description" value={description}
                               onChange={(event) => setDescription(event.target.value)} placeholder="장소 소개"/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="place-category">카테고리</Label>
                        <Input id="place-category" value={category}
                               onChange={(event) => setCategory(event.target.value)} placeholder="예: 맛집/카페"/>
                    </div>
                    <DialogFooter>
                        <DialogClose
                            render={<Button type="button" variant="outline" disabled={isSubmitting}>취소</Button>}/>
                        <Button type="submit"
                                disabled={!thumbnailUrl || !title.trim() || !description.trim() || !category.trim() || isSubmitting}>
                            {isSubmitting ? "등록 중..." : "장소 등록"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

function SettlementExpenseDialog({
                                     open,
                                     onOpenChange,
                                     workspaceId,
                                     onCreated,
                                 }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    workspaceId: string;
    onCreated: (expense: SettlementExpenseDTO) => void;
}) {
    const [item, setItem] = useState("");
    const [payer, setPayer] = useState("");
    const [amount, setAmount] = useState("");
    const [status, setStatus] = useState<SettlementStatus>("PENDING");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const numericAmount = Number(amount);
    const canSubmit = Boolean(item.trim() && payer.trim() && Number.isFinite(numericAmount) && numericAmount > 0 && !isSubmitting);

    const reset = () => {
        setItem("");
        setPayer("");
        setAmount("");
        setStatus("PENDING");
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        if (!canSubmit) return;
        setIsSubmitting(true);
        try {
            onCreated(await createSettlementExpense({
                workspaceId,
                item: item.trim(),
                payer: payer.trim(),
                amount: numericAmount,
                status,
            }));
            reset();
            onOpenChange(false);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={(next) => {
            if (!next) reset();
            onOpenChange(next);
        }}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>내역 추가</DialogTitle>
                    <DialogDescription>등록한 내역은 정산 요약과 상세 표에 즉시 반영됩니다.</DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="expense-item">항목</Label>
                        <Input id="expense-item" value={item} onChange={(event) => setItem(event.target.value)}
                               placeholder="예: 숙소 예약" autoFocus/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="expense-payer">결제자</Label>
                        <Input id="expense-payer" value={payer} onChange={(event) => setPayer(event.target.value)}
                               placeholder="예: 민지"/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="expense-amount">금액</Label>
                        <Input id="expense-amount" type="number" min="1" value={amount}
                               onChange={(event) => setAmount(event.target.value)} placeholder="예: 120000"/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="expense-status">상태</Label>
                        <select
                            id="expense-status"
                            value={status}
                            onChange={(event) => setStatus(event.target.value as SettlementStatus)}
                            className="flex h-8 w-full rounded-lg border border-input bg-transparent px-2.5 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50"
                        >
                            {Object.entries(EXPENSE_STATUS).map(([value, {label}]) => <option key={value}
                                                                                              value={value}>{label}</option>)}
                        </select>
                    </div>
                    <DialogFooter>
                        <DialogClose
                            render={<Button type="button" variant="outline" disabled={isSubmitting}>취소</Button>}/>
                        <Button type="submit" disabled={!canSubmit}>{isSubmitting ? "등록 중..." : "내역 등록"}</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}


const formatPollDate = (date: string) =>
    new Intl.DateTimeFormat("ko-KR", {month: "long", day: "numeric", weekday: "short"})
        .format(new Date(`${date}T00:00:00`));


const formatPollPeriod = (startDt: string, endDt: string) =>
    startDt === endDt ? formatPollDate(startDt) : `${formatPollDate(startDt)} – ${formatPollDate(endDt)}`;


function PlannerDetailPage() {
    const {currentWorkspace} = useAuthStore();
    const {plannerId} = useParams<{ plannerId: string }>();
    const router = useRouter();
    const workspaceId = currentWorkspace?.id;
    const plannerWorkspaceId = workspaceId ?? "";
    const [planner, setPlanner] = useState<PlannerDTO | null>(null);
    const [calendarDate, setCalendarDate] = useState(() => new Date());
    const [pollOpen, setPollOpen] = useState(false);
    const [schedulePolls, setSchedulePolls] = useState<SchedulePollDTO[]>([]);
    const [topPollVote, setTopPollVote] = useState<SchedulePollVoteSummaryDTO | null>(null);
    const [isPollLoading, setIsPollLoading] = useState(false);
    const [placeOpen, setPlaceOpen] = useState(false);
    const [places, setPlaces] = useState<PlaceSuggestionDTO[]>([]);
    const [isPlaceLoading, setIsPlaceLoading] = useState(false);
    const [expenseOpen, setExpenseOpen] = useState(false);
    const [expenses, setExpenses] = useState<SettlementExpenseDTO[]>([]);
    const [isExpenseLoading, setIsExpenseLoading] = useState(false);
    const month = `${calendarDate.getFullYear()}년 ${calendarDate.getMonth() + 1}월`;

    useEffect(() => {
        getPlanner(plannerWorkspaceId, plannerId).then((item) => {
            setPlanner(item);
            if (item) setCalendarDate(new Date(item.calendar.year, item.calendar.month - 1, 1));
        });
    }, [plannerId, plannerWorkspaceId]);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setIsPollLoading(true);
            try {
                const [polls, summaries] = await Promise.all([
                    getSchedulePolls(plannerWorkspaceId),
                    getSchedulePollVoteSummary(plannerWorkspaceId),
                ]);
                if (!cancelled) {
                    setSchedulePolls(polls);
                    setTopPollVote(summaries[0] ?? null);
                }
            } finally {
                if (!cancelled) setIsPollLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [plannerWorkspaceId]);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setIsExpenseLoading(true);
            try {
                const result = await getSettlementExpenses(plannerWorkspaceId);
                if (!cancelled) setExpenses(result);
            } finally {
                if (!cancelled) setIsExpenseLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [plannerWorkspaceId]);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setIsPlaceLoading(true);
            try {
                const suggestions = await getPlaceSuggestions(plannerWorkspaceId);
                if (!cancelled) setPlaces(suggestions);
            } finally {
                if (!cancelled) setIsPlaceLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [plannerWorkspaceId]);

    const shiftMonth = (amount: -1 | 1) => {
        setCalendarDate((date) => new Date(date.getFullYear(), date.getMonth() + amount, 1));
    };

    const handlePollVote = async (pollId: string, candidateId: string) => {
        try {
            const poll = await toggleSchedulePollVote(pollId, candidateId);
            setSchedulePolls((polls) => polls.map((item) => item.id === poll.id ? poll : item));
            const summaries = await getSchedulePollVoteSummary(plannerWorkspaceId);
            setTopPollVote(summaries[0] ?? null);
        } catch {
            // ponytail: add a toast when the real API can return a failure state.
        }
    };

    const handlePlaceVote = async (placeId: string) => {
        const place = await togglePlaceSuggestionVote(placeId);
        setPlaces((current) => current.map((item) => item.id === place.id ? place : item));
    };

    const totalExpense = expenses.reduce((total, expense) => total + expense.amount, 0);

    return (
        <div className="flex h-full min-h-0 flex-1 flex-col overflow-hidden">
            <header
                className="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-4 lg:px-8">
                <div className="flex items-center gap-3">
                    <Link href="/planner" className="text-lg font-bold text-primary">Kinfolk Table</Link>
                    <span className="hidden h-6 w-px bg-border sm:block"/>
                    <span className="hidden text-sm text-muted-foreground sm:block">게스트 플래너</span>
                    <Link href="/planner"
                          className="hidden items-center gap-1 text-sm text-muted-foreground hover:text-primary md:flex"><Home
                        size={14}/> 대시보드로 돌아가기</Link>
                </div>
                <nav className="hidden gap-6 text-sm font-medium md:flex">
                    <a href="#itinerary" className="text-primary">여행 정보</a>
                    <a href="#vote" className="text-muted-foreground hover:text-primary">투표</a>
                    <a href="#settlement" className="text-muted-foreground hover:text-primary">정산</a>
                </nav>
            </header>

            <main className="flex-1 overflow-y-auto">
                <div className="mx-auto max-w-[1440px] space-y-6 px-4 py-6 lg:px-8 lg:py-12">
                    <header className="flex flex-col justify-between gap-4 md:flex-row md:items-end">
                        <div>
                            <h1 className="text-3xl font-bold tracking-tight text-primary lg:text-[40px]">{planner?.title ?? "플래너"}</h1>
                            <p className="mt-2 text-sm text-muted-foreground lg:text-base">함께 가는
                                사람들: {planner?.participants.join(", ") || "등록된 사람이 없습니다."}</p>
                        </div>
                        <div className="flex items-center gap-3">
                            <div className="flex -space-x-2">
                                {planner?.participants.slice(0, 3).map((participant, index) => (
                                    <Avatar key={participant} size="lg"><AvatarFallback
                                        className={["bg-[#d5e3fc]", "bg-[#d4e4fa]", "bg-[#dae3f0]"][index]}>{participant.slice(0, 1)}</AvatarFallback></Avatar>
                                ))}
                                {(planner?.participants.length ?? 0) > 3 && <span
                                    className="flex size-10 items-center justify-center rounded-full bg-muted text-xs text-muted-foreground ring-2 ring-background">+{planner!.participants.length - 3}</span>}
                            </div>
                            <Button variant="outline" className="rounded-full"><UserPlus size={15}/> 게스트 초대하기</Button>
                        </div>
                    </header>

                    <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
                        <Card id="itinerary"
                              className="gap-0 border-white/70 bg-card/70 py-0 shadow-sm backdrop-blur lg:col-span-7">
                            <CardHeader className="p-5 lg:p-6">
                                <div className="flex items-center justify-between gap-3">
                                    <h2 className="flex items-center gap-2 text-xl font-semibold text-primary"><Route
                                        size={21}/> 여행 일정 짜기</h2>
                                    <Button size="sm" variant="outline" className="rounded-full"
                                            onClick={() => router.push(`/planner/${plannerId}/edit`)}><Plus
                                        size={15}/> 일정 추가</Button>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-4 pb-5 lg:pb-6">
                                {schedulePolls.length > 0 ? schedulePolls.flatMap((poll) => poll.candidates.map((candidate) => ({
                                    candidate,
                                    title: poll.title
                                }))).slice(0, 3).map(({candidate, title}, index) => (
                                    <div key={candidate.id} className="border-l-2 border-border pl-4">
                                        <div className="flex items-center gap-2"><Chip size="sm"
                                                                                       color={index === 0 ? "slate" : "default"}>Day {index + 1}</Chip><span
                                            className="text-sm font-medium">{formatPollPeriod(candidate.startDt, candidate.endDt)}</span>
                                        </div>
                                        <p className="mt-2 rounded-lg bg-muted/70 p-3 text-sm text-foreground">{title}</p>
                                    </div>
                                )) : <div
                                    className="rounded-lg border border-dashed border-border p-8 text-center text-sm text-muted-foreground">일정을
                                    추가해 여행 동선을 만들어 보세요.</div>}
                            </CardContent>
                        </Card>

                        <Card id="vote"
                              className="gap-0 border-white/70 bg-card/70 py-0 shadow-sm backdrop-blur lg:col-span-5">
                            <CardHeader className="p-5 lg:p-6">
                                <div className="flex items-center justify-between gap-3">
                                    <div>
                                        <h3 className="flex items-center gap-2 text-lg font-semibold text-primary">
                                            <CalendarDays size={19} strokeWidth={1.8}/> 희망 일정 투표
                                        </h3>
                                        <p className="mt-1 text-sm text-muted-foreground">{month}</p>
                                    </div>
                                    <div className="flex items-center gap-1">
                                        <Button variant="ghost" size="icon" aria-label="이전 달"
                                                onClick={() => shiftMonth(-1)}>
                                            <ChevronLeft size={18}/>
                                        </Button>
                                        <Button variant="ghost" size="icon" aria-label="다음 달"
                                                onClick={() => shiftMonth(1)}>
                                            <ChevronRight size={18}/>
                                        </Button>
                                        <Button size="sm" className="ml-1" onClick={() => setPollOpen(true)}
                                                disabled={!workspaceId}>
                                            <Plus size={15}/> 희망 일정 등록
                                        </Button>
                                    </div>
                                </div>
                            </CardHeader>

                            <CardContent className="space-y-5 pb-5 lg:pb-6">
                                <CommCalendar
                                    year={calendarDate.getFullYear()}
                                    month={calendarDate.getMonth() + 1}
                                />

                                <div className="space-y-2">
                                    <div className="flex items-center justify-between">
                                        <p className="text-sm font-semibold text-foreground">등록된 희망 일정</p>
                                        <Chip size="sm" color="slate">익명 투표</Chip>
                                    </div>
                                    {isPollLoading &&
                                        <p className="py-2 text-center text-xs text-muted-foreground">불러오는 중...</p>}
                                    {!isPollLoading && schedulePolls.length === 0 &&
                                        <p className="py-2 text-center text-xs text-muted-foreground">등록된 희망 일정이
                                            없습니다.</p>}
                                    {schedulePolls.flatMap((poll) => poll.candidates.map((candidate) => (
                                        <button
                                            key={candidate.id}
                                            type="button"
                                            onClick={() => handlePollVote(poll.id, candidate.id)}
                                            aria-pressed={candidate.votedByMe}
                                            className={cn(
                                                "w-full rounded-lg border p-3 text-left transition-colors",
                                                candidate.votedByMe
                                                    ? "border-primary bg-primary/10"
                                                    : "border-border bg-muted/30 hover:bg-muted/60"
                                            )}
                                        >
                                            <div className="flex items-center justify-between gap-3">
                                                <p className="text-sm font-medium text-foreground">{poll.title}</p>
                                                <span
                                                    className={cn("text-xs font-semibold", candidate.votedByMe ? "text-primary" : "text-muted-foreground")}>
                          {candidate.voteCount}표
                        </span>
                                            </div>
                                            <p className="mt-1 text-xs text-muted-foreground">{formatPollPeriod(candidate.startDt, candidate.endDt)}</p>
                                        </button>
                                    )))}
                                </div>

                                <div
                                    className="flex items-center gap-3 rounded-lg bg-muted/70 p-3 text-sm text-muted-foreground">
                                    <Info size={18} className="shrink-0 text-secondary-foreground"/>
                                    {isPollLoading
                                        ? "투표 결과를 불러오는 중..."
                                        : topPollVote
                                            ? `${formatPollPeriod(topPollVote.startDt, topPollVote.endDt)} 일정이 ${topPollVote.voteCount}표로 가장 많습니다.`
                                            : "아직 등록된 희망 일정이 없습니다."}
                                </div>

                            </CardContent>
                        </Card>

                        <Card id="settlement"
                              className="order-4 gap-0 border-0 bg-primary py-0 text-primary-foreground shadow-lg lg:col-span-4">
                            <CardHeader className="mb-8 p-5 lg:p-6">
                                <div className="flex items-start justify-between">
                                    <h3 className="text-lg font-semibold">정산하기</h3>
                                    <WalletCards size={21} strokeWidth={1.8}/>
                                </div>
                            </CardHeader>
                            <CardContent className="flex flex-1 flex-col px-5 pb-5 lg:px-6 lg:pb-6">
                                <div className="space-y-4">
                                    <div className="flex items-center justify-between border-b border-white/20 pb-4">
                                        <span className="text-sm text-white/75">총 지출</span>
                                        <strong className="text-2xl">{formatCurrency(totalExpense)}</strong>
                                    </div>
                                    {isExpenseLoading && <p className="text-sm text-white/70">내역을 불러오는 중...</p>}
                                    {expenses.slice(0, 3).map((expense) => (
                                        <div key={expense.id}
                                             className="flex items-center justify-between gap-3 text-sm">
                                            <span>{expense.item}</span>
                                            <span
                                                className="font-medium text-[#d5e3fc]">{formatCurrency(expense.amount)}</span>
                                        </div>
                                    ))}
                                </div>
                                <Button className="mt-6 w-full bg-card text-primary hover:bg-card/90"
                                        onClick={() => setExpenseOpen(true)}>내역 추가</Button>
                            </CardContent>
                        </Card>

                        <section className="order-3 space-y-4 lg:col-span-12">
                            <div className="flex items-center justify-between gap-3">
                                <h3 className="flex items-center gap-2 text-lg font-semibold text-primary">
                                    <MapPinned size={19} strokeWidth={1.8}/> 추천 장소 투표
                                </h3>
                                <Button variant="outline" className="rounded-full"
                                        onClick={() => setPlaceOpen(true)}><Plus size={15}/> 장소 제안하기</Button>
                            </div>
                            <div className="flex flex-col gap-4 sm:flex-row">
                                <Button type="button" variant="outline"
                                        className="h-64 w-full shrink-0 flex-col border-2 border-dashed text-muted-foreground hover:bg-muted/60 sm:w-52"
                                        onClick={() => setPlaceOpen(true)}>
                                    <span
                                        className="mb-3 flex size-12 items-center justify-center rounded-full bg-muted"><Plus
                                        size={22}/></span>
                                    <span className="text-sm font-medium">장소 추가하기</span>
                                </Button>
                                {places.length === 0 && !isPlaceLoading ? (
                                    <div
                                        className="flex h-64 flex-1 items-center justify-center rounded-xl border border-dashed border-border bg-muted/30 px-6 text-center text-sm text-muted-foreground">
                                        장소를 추천해주세요.
                                    </div>
                                ) : (
                                    <Carousel opts={{align: "start"}} className="min-w-0 flex-1 px-8">
                                        <CarouselContent className="-ml-4">
                                            {places.map((place) => (
                                                <CarouselItem key={place.id}
                                                              className="basis-[82%] pl-4 sm:basis-1/2 lg:basis-1/3 xl:basis-1/4">
                                                    <Card className="h-64 gap-0 py-0 transition-shadow hover:shadow-lg">
                                                        <div
                                                            className="relative h-36 shrink-0 bg-muted bg-cover bg-center"
                                                            style={{backgroundImage: `url(${place.thumbnailUrl})`}}
                                                        >
                                                            <Chip color="slate" size="sm"
                                                                  className="absolute left-3 top-3 bg-card/90 backdrop-blur">
                                                                {place.category}
                                                            </Chip>
                                                        </div>
                                                        <CardContent
                                                            className="flex flex-1 flex-col justify-between space-y-2 p-4">
                                                            <div>
                                                                <a href={place.sourceUrl} target="_blank"
                                                                   rel="noreferrer"
                                                                   className="flex items-center gap-1 font-semibold text-primary hover:underline">
                                                                    {place.title} <ExternalLink size={13}/>
                                                                </a>
                                                                <p className="mt-1 line-clamp-2 text-xs leading-4 text-muted-foreground">{place.description}</p>
                                                            </div>
                                                            <div className="flex items-center justify-between gap-2">
                                                                <span
                                                                    className="text-xs text-muted-foreground">{place.voteCount}명 투표</span>
                                                                <Button
                                                                    size="sm"
                                                                    variant={place.votedByMe ? "destructive" : "secondary"}
                                                                    className={cn("rounded-full", !place.votedByMe && "bg-muted text-muted-foreground hover:bg-secondary")}
                                                                    onClick={() => handlePlaceVote(place.id)}
                                                                    aria-pressed={place.votedByMe}
                                                                >
                                                                    <Heart size={14}
                                                                           fill={place.votedByMe ? "currentColor" : "none"}/> 좋아요
                                                                </Button>
                                                            </div>
                                                        </CardContent>
                                                    </Card>
                                                </CarouselItem>
                                            ))}
                                        </CarouselContent>
                                        <CarouselPrevious className="left-0 bg-card/90 backdrop-blur"/>
                                        <CarouselNext className="right-0 bg-card/90 backdrop-blur"/>
                                    </Carousel>
                                )}
                            </div>
                            {isPlaceLoading &&
                                <p className="text-center text-xs text-muted-foreground">장소를 불러오는 중...</p>}
                        </section>

                        <Card
                            className="order-5 gap-0 border-white/70 bg-card/70 py-0 shadow-sm backdrop-blur lg:col-span-8">
                            <CardHeader className="p-5 pb-0 lg:p-6 lg:pb-0">
                                <h3 className="flex items-center gap-2 text-lg font-semibold text-primary">
                                    <ReceiptText size={19} strokeWidth={1.8}/> 정산 내역 상세
                                </h3>
                            </CardHeader>
                            <CardContent className="p-5 lg:p-6">
                                <Table className="min-w-[600px] text-left">
                                    <TableHeader className="text-xs text-muted-foreground">
                                        <TableRow>
                                            <TableHead>날짜</TableHead><TableHead>항목</TableHead><TableHead>결제자</TableHead><TableHead>금액</TableHead><TableHead
                                            className="text-right">상태</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {expenses.map((expense, index) => (
                                            <TableRow key={expense.id}>
                                                <TableCell
                                                    className="py-4 text-muted-foreground">{expense.date.replaceAll("-", ".")}</TableCell>
                                                <TableCell className="py-4 font-semibold">{expense.item}</TableCell>
                                                <TableCell className="py-4"><span
                                                    className="flex items-center gap-2"><Avatar
                                                    size="sm"><AvatarFallback
                                                    className={["bg-[#d5e3fc]", "bg-[#d4e4fa]", "bg-[#dae3f0]"][index % 3]}/></Avatar>{expense.payer}</span></TableCell>
                                                <TableCell className="py-4">{formatCurrency(expense.amount)}</TableCell>
                                                <TableCell className="py-4 text-right"><Chip
                                                    color={EXPENSE_STATUS[expense.status].chip}
                                                    size="sm">{EXPENSE_STATUS[expense.status].label}</Chip></TableCell>
                                            </TableRow>
                                        ))}
                                        {!isExpenseLoading && expenses.length === 0 && (
                                            <TableRow><TableCell colSpan={5}
                                                                 className="py-8 text-center text-muted-foreground">등록된
                                                정산 내역이 없습니다.</TableCell></TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </main>

            {/*<button type="button" aria-label="그룹 채팅" className="fixed bottom-20 right-5 flex size-14 items-center justify-center rounded-full bg-primary text-primary-foreground shadow-xl transition-transform hover:scale-105 lg:bottom-8 lg:right-8">*/}
            {/*  <MessageCircle size={23} />*/}
            {/*  <span className="absolute -right-1 -top-1 flex size-5 items-center justify-center rounded-full bg-destructive text-[10px] font-bold text-destructive-foreground">3</span>*/}
            {/*</button>*/}

            <SchedulePollDialog
                open={pollOpen}
                onOpenChange={setPollOpen}
                workspaceId={plannerWorkspaceId}
                onCreated={(poll) => {
                    setSchedulePolls((polls) => [poll, ...polls]);
                    getSchedulePollVoteSummary(plannerWorkspaceId).then((summaries) => setTopPollVote(summaries[0] ?? null));
                }}
            />
            <PlaceSuggestionDialog
                open={placeOpen}
                onOpenChange={setPlaceOpen}
                workspaceId={plannerWorkspaceId}
                onCreated={(place) => setPlaces((current) => [place, ...current])}
            />
            <SettlementExpenseDialog
                open={expenseOpen}
                onOpenChange={setExpenseOpen}
                workspaceId={plannerWorkspaceId}
                onCreated={(expense) => setExpenses((current) => [expense, ...current])}
            />
        </div>
    );
}

export default PlannerDetailPage;
