"use client";

import Link from "next/link";
import {useParams, useRouter} from "next/navigation";
import {useEffect, useState, type FormEvent, type KeyboardEvent} from "react";
import {
    CalendarDays,
    ArrowUpRight,
    ChevronLeft,
    ChevronRight,
    ExternalLink,
    Heart,
    Info,
    Home,
    MapPinned,
    MessageCircle,
    Plus,
    ReceiptText,
    Route,
    UserPlus,
    UsersRound,
    WalletCards,
    X,
} from "lucide-react";
import {TopBar} from "@/shared/ui/top-bar";
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
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    Input,
    Label,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/shared/ui";
import {cn} from "@/shared/utils";
import {
    createPlanner,
    getPlanners,
    type PlannerDTO,
} from "@/shared/api";
import {useAuthStore} from "@/stores/auth-store";


function AddPlannerDialog({
                              open,
                              onOpenChange,
                              workspaceId,
                              onCreated,
                          }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    workspaceId: string;
    onCreated: (planner: PlannerDTO) => void;
}) {
    const [title, setTitle] = useState("");
    const [participantInput, setParticipantInput] = useState("");
    const [participants, setParticipants] = useState<string[]>([]);

    const reset = () => {
        setTitle("");
        setParticipantInput("");
        setParticipants([]);
    };

    const addParticipant = () => {
        const name = participantInput.trim();
        if (!name || participants.includes(name)) return;
        setParticipants((current) => [...current, name]);
        setParticipantInput("");
    };

    const handleParticipantKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
        if (event.key !== "Enter") return;
        event.preventDefault();
        addParticipant();
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        const trimmedTitle = title.trim();
        if (!trimmedTitle) return;
        const finalParticipants = participantInput.trim() && !participants.includes(participantInput.trim())
            ? [...participants, participantInput.trim()]
            : participants;
        onCreated(await createPlanner({workspaceId, title: trimmedTitle, participants: finalParticipants}));
        reset();
        onOpenChange(false);
    };

    return (
        <Dialog open={open} onOpenChange={(next) => {
            if (!next) reset();
            onOpenChange(next);
        }}>
            <DialogContent className="max-w-lg">
                <DialogHeader>
                    <DialogTitle>플래너 추가</DialogTitle>
                    <DialogDescription>여행이나 모임을 함께 준비할 사람을 추가하세요.</DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="planner-title">플래너 제목</Label>
                        <Input id="planner-title" value={title} onChange={(event) => setTitle(event.target.value)}
                               placeholder="예: 부산 주말 여행" autoFocus/>
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="planner-participants">함께 가는 사람</Label>
                        <div className="flex gap-2">
                            <Input
                                id="planner-participants"
                                value={participantInput}
                                onChange={(event) => setParticipantInput(event.target.value)}
                                onKeyDown={handleParticipantKeyDown}
                                placeholder="이름 입력 후 Enter"
                            />
                            <Button type="button" variant="outline" onClick={addParticipant}>추가</Button>
                        </div>
                        {participants.length > 0 && (
                            <div className="flex flex-wrap gap-2 pt-1">
                                {participants.map((participant) => (
                                    <Chip key={participant} color="slate" className="pr-1">
                                        {participant}
                                        <button
                                            type="button"
                                            onClick={() => setParticipants((current) => current.filter((name) => name !== participant))}
                                            aria-label={`${participant} 삭제`}
                                            className="rounded-full p-0.5 hover:bg-primary/10"
                                        >
                                            <X size={12}/>
                                        </button>
                                    </Chip>
                                ))}
                            </div>
                        )}
                    </div>
                    <DialogFooter>
                        <DialogClose render={<Button type="button" variant="outline">취소</Button>}/>
                        <Button type="submit" disabled={!title.trim()}>플래너 만들기</Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

function PlannerListPage() {
    const {currentWorkspace} = useAuthStore();
    const workspaceId = currentWorkspace?.id ?? "";
    const [planners, setPlanners] = useState<PlannerDTO[]>([]);
    const [addOpen, setAddOpen] = useState(false);

    useEffect(() => {
        getPlanners(workspaceId).then(setPlanners);
    }, [workspaceId]);

    return (
        <div className="flex h-full min-h-0 flex-1 flex-col overflow-hidden">
            <TopBar/>
            <main className="flex-1 overflow-y-auto">
                <div className="mx-auto max-w-6xl px-4 py-5 lg:px-8 lg:py-8">
                    <header className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Shared
                                plans</p>
                            <h1 className="mt-1 text-2xl font-bold tracking-tight text-primary lg:text-[32px]">플래너</h1>
                            <p className="mt-2 text-sm text-muted-foreground lg:text-base">함께 갈 사람들과 여행과 모임을 계획하세요.</p>
                        </div>
                        <Button onClick={() => setAddOpen(true)}><Plus size={16}/> 플래너 추가</Button>
                    </header>

                    <section className="mt-7 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3" aria-label="플래너 목록">
                        {planners.map((planner) => (
                            <Link key={planner.id} href={`/planner/${planner.id}`}
                                  className="group block focus-visible:outline-none">
                                <Card
                                    className="h-full gap-0 py-0 transition-all hover:-translate-y-0.5 hover:shadow-lg group-focus-visible:ring-2 group-focus-visible:ring-ring">
                                    <CardHeader className="p-5 pb-4">
                                        <div className="flex items-start justify-between gap-3">
                      <span
                          className={`flex size-10 items-center justify-center rounded-xl ${planner.color === "blue" ? "bg-[#d2e1f7] text-[#516072]" : planner.color === "green" ? "bg-[#d1f5e4] text-[#2e7d5a]" : "bg-[#ead6f0] text-[#7c4d8a]"}`}>
                        <MapPinned size={19}/>
                      </span>
                                            <ArrowUpRight size={18}
                                                          className="text-muted-foreground transition-transform group-hover:-translate-y-0.5 group-hover:translate-x-0.5"/>
                                        </div>
                                        <h2 className="mt-5 text-lg font-semibold text-primary">{planner.title}</h2>
                                    </CardHeader>
                                    <CardContent className="space-y-4 pb-5">
                                        <div>
                                            <p className="mb-2 flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                                                <UsersRound size={14}/> 함께 가는 사람</p>
                                            <div className="flex flex-wrap gap-1.5">
                                                {planner.participants.length > 0 ? planner.participants.slice(0, 4).map((participant) =>
                                                    <Chip key={participant} color={planner.color}
                                                          size="sm">{participant}</Chip>) : <span
                                                    className="text-sm text-muted-foreground">아직 추가된 사람이 없습니다.</span>}
                                                {planner.participants.length > 4 &&
                                                    <Chip size="sm">+{planner.participants.length - 4}</Chip>}
                                            </div>
                                        </div>
                                        <p className="text-xs text-muted-foreground">{new Intl.DateTimeFormat("ko-KR", {
                                            month: "long",
                                            day: "numeric"
                                        }).format(new Date(planner.updatedAt))} 업데이트</p>
                                    </CardContent>
                                </Card>
                            </Link>
                        ))}
                    </section>
                </div>
            </main>
            <AddPlannerDialog workspaceId={workspaceId} open={addOpen} onOpenChange={setAddOpen}
                              onCreated={(planner) => setPlanners((current) => [planner, ...current])}/>
        </div>
    );
}

export default PlannerListPage;
