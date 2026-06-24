"use client";

import Link from "next/link";
import {useEffect, useState} from "react";
import {useParams, useRouter} from "next/navigation";
import {ArrowLeft, CalendarDays, FileDown, MapPin, Plus, Share2, Trash2} from "lucide-react";
import {
    Button,
    Card,
    CardContent,
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    Input,
    Textarea
} from "@/shared/ui";
import {
    getPlanner,
    updatePlannerItinerary,
    type PlannerDTO,
    type PlannerItineraryDayDTO
} from "@/shared/api";

const newDay = (): PlannerItineraryDayDTO => ({
    id: `day-${Date.now()}`,
    date: new Date().toISOString().slice(0, 10),
    title: "새로운 일정",
    items: [],
});

const newItem = (itemCount = 0) => ({
    id: `activity-${Date.now()}`,
    time: `${String(Math.min(23, 9 + itemCount)).padStart(2, "0")}:00`,
    category: "일정",
    title: "새 일정",
    description: "일정 내용을 입력하세요.",
});
import {useAuthStore} from "@/stores/auth-store";

type DeletionTarget = { dayId: string; activityId?: string };

export default function PlannerEditPage() {
    const {currentWorkspace} = useAuthStore();
    const {plannerId} = useParams<{ plannerId: string }>();
    const router = useRouter();
    const workspaceId = currentWorkspace?.id ?? "";
    const [planner, setPlanner] = useState<PlannerDTO | null>(null);
    const [itinerary, setItinerary] = useState<PlannerItineraryDayDTO[]>([]);
    const [deletionTarget, setDeletionTarget] = useState<DeletionTarget | null>(null);

    useEffect(() => {
        getPlanner(workspaceId, plannerId).then((item) => {
            setPlanner(item);
            setItinerary(item?.itinerary ?? []);
        });
    }, [plannerId, workspaceId]);

    const addActivity = (dayId: string) => {
        setItinerary((days) => days.map((day) => day.id === dayId ? {
            ...day,
            items: [...day.items, newItem(day.items.length)],
        } : day));
    };

    const updateDay = (dayId: string, patch: Partial<PlannerItineraryDayDTO>) => {
        setItinerary((days) => days.map((day) => day.id === dayId ? {...day, ...patch} : day));
    };

    const updateActivity = (dayId: string, activityId: string, patch: Record<string, string>) => {
        setItinerary((days) => days.map((day) => day.id === dayId ? {
            ...day,
            items: day.items.map((item) => item.id === activityId ? {...item, ...patch} : item),
        } : day));
    };

    const confirmDeletion = () => {
        if (!deletionTarget) return;
        setItinerary((days) => deletionTarget.activityId
            ? days.map((day) => day.id === deletionTarget.dayId ? {
                ...day,
                items: day.items.filter((item) => item.id !== deletionTarget.activityId)
            } : day)
            : days.filter((day) => day.id !== deletionTarget.dayId));
        setDeletionTarget(null);
    };

    const save = async () => {
        await updatePlannerItinerary(workspaceId, plannerId, itinerary);
        router.push(`/planner/${plannerId}`);
    };

    return (
        <main className="flex-1 overflow-y-auto bg-background px-4 py-6 lg:px-16 lg:py-12">
            <div className="mx-auto max-w-5xl">
                <header className="mb-10 flex flex-col justify-between gap-5 md:flex-row md:items-end">
                    <div>
                        <Link href={`/planner/${plannerId}`}
                              className="mb-2 inline-flex items-center gap-1 text-sm font-medium text-primary hover:underline"><ArrowLeft
                            size={17}/> 전체 일정으로 돌아가기</Link>
                        <h1 className="text-3xl font-bold tracking-tight text-primary lg:text-[40px]">여행 일정 짜기</h1>
                        <p className="mt-2 text-sm text-muted-foreground lg:text-base">{planner?.title ?? "플래너"}</p>
                    </div>
                    <div className="flex gap-2">
                        <Button variant="outline"><FileDown size={16}/> PDF로 저장</Button>
                        <Button><Share2 size={16}/> 공유하기</Button>
                    </div>
                </header>

                <div className="space-y-10">
                    {itinerary.map((day, index) => (
                        <section key={day.id} className="space-y-5">
                            <div className="flex flex-col justify-between gap-2 sm:flex-row sm:items-center">
                                <div className="flex items-center gap-3">
                                    <span
                                        className="flex size-10 items-center justify-center rounded-full bg-primary font-bold text-primary-foreground">{index + 1}</span>
                                    <div className="flex items-center gap-2 text-xl font-semibold text-primary">
                                        <span>Day {index + 1} —</span><Input value={day.title}
                                                                             onChange={(event) => updateDay(day.id, {title: event.target.value})}
                                                                             aria-label={`Day ${index + 1} 제목`}
                                                                             className="h-9 w-56 border-transparent bg-transparent px-1 text-xl font-semibold shadow-none hover:border-border focus-visible:border-ring"/>
                                    </div>
                                </div>
                                <div className="flex items-center gap-1">
                                    <Input type="date" value={day.date}
                                           onChange={(event) => updateDay(day.id, {date: event.target.value})}
                                           aria-label={`Day ${index + 1} 날짜`}
                                           className="h-8 w-36 border-transparent bg-transparent px-1 text-sm text-muted-foreground shadow-none hover:border-border focus-visible:border-ring"/>
                                    <Button size="icon-xs" variant="ghost"
                                            className="text-destructive hover:text-destructive"
                                            onClick={() => setDeletionTarget({dayId: day.id})}
                                            aria-label={`${day.title} 삭제`}><Trash2 size={14}/></Button>
                                </div>
                            </div>

                            <div className="relative ml-5 space-y-4 border-l-2 border-muted pl-7">
                                {day.items.map((item, itemIndex) => (
                                    <div key={item.id} className="relative flex gap-4">
                                        <span
                                            className={`absolute -left-[35px] top-4 size-4 rounded-full border-4 border-background ${itemIndex === 0 ? "bg-primary" : "bg-secondary"}`}/>
                                        <Input value={item.time}
                                               onChange={(event) => updateActivity(day.id, item.id, {time: event.target.value})}
                                               aria-label={`${item.title} 시간`}
                                               className="mt-3 h-8 w-20 shrink-0 border-transparent bg-transparent px-1 text-xs font-medium text-muted-foreground shadow-none hover:border-border focus-visible:border-ring sm:text-sm"/>
                                        <Card
                                            className="flex-1 border-white/70 bg-card/80 py-0 shadow-sm backdrop-blur transition-shadow hover:shadow-md">
                                            <CardContent className="p-4">
                                                <div
                                                    className="flex items-center gap-1.5 text-xs font-semibold tracking-wide text-secondary">
                                                    <MapPin size={14}/><Input value={item.category}
                                                                              onChange={(event) => updateActivity(day.id, item.id, {category: event.target.value})}
                                                                              aria-label={`${item.title} 분류`}
                                                                              className="h-6 w-24 border-transparent bg-transparent px-0 text-xs font-semibold text-secondary shadow-none hover:border-border focus-visible:border-ring"/>
                                                </div>
                                                <div className="mt-2 flex items-start justify-between gap-3">
                                                    <Input value={item.title}
                                                           onChange={(event) => updateActivity(day.id, item.id, {title: event.target.value})}
                                                           aria-label="일정 제목"
                                                           className="h-8 border-transparent bg-transparent px-0 text-base font-semibold shadow-none hover:border-border focus-visible:border-ring"/>
                                                    <span className="flex gap-1">
                            <Button size="icon-xs" variant="ghost" className="text-destructive hover:text-destructive"
                                    onClick={() => setDeletionTarget({dayId: day.id, activityId: item.id})}
                                    aria-label={`${item.title} 삭제`}><Trash2 size={14}/></Button>
                          </span>
                                                </div>
                                                <Textarea value={item.description}
                                                          onChange={(event) => updateActivity(day.id, item.id, {description: event.target.value})}
                                                          aria-label={`${item.title} 설명`}
                                                          className="mt-1 min-h-12 resize-none border-transparent bg-transparent px-0 py-0 text-sm leading-6 text-muted-foreground shadow-none hover:border-border focus-visible:border-ring"/>
                                            </CardContent>
                                        </Card>
                                    </div>
                                ))}
                                <Button type="button" variant="outline" className="border-dashed"
                                        onClick={() => addActivity(day.id)}><Plus size={16}/> 일정 추가하기</Button>
                            </div>
                        </section>
                    ))}

                    {itinerary.length === 0 && <Card className="border-dashed py-0"><CardContent
                        className="p-10 text-center text-sm text-muted-foreground">아직 만든 일정이 없습니다. 새로운 날짜를 추가해
                        시작하세요.</CardContent></Card>}

                    <div className="flex justify-center">
                        <Button type="button" variant="outline"
                                className="h-auto flex-col border-2 border-dashed px-10 py-6"
                                onClick={() => setItinerary((days) => [...days, newDay()])}>
                            <CalendarDays size={28}/> 새로운 날짜 추가하기
                        </Button>
                    </div>

                    <div className="flex justify-end gap-2 border-t border-border pt-6">
                        <Button variant="outline" onClick={() => router.back()}>취소</Button>
                        <Button onClick={save}>완료</Button>
                    </div>
                </div>
            </div>
            <Dialog open={deletionTarget !== null} onOpenChange={(open) => {
                if (!open) setDeletionTarget(null);
            }}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{deletionTarget?.activityId ? "일정 삭제" : "날짜 삭제"}</DialogTitle>
                        <DialogDescription>{deletionTarget?.activityId ? "이 일정은 복구할 수 없습니다." : "이 날짜와 포함된 모든 일정이 삭제됩니다."}</DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <DialogClose render={<Button variant="outline">취소</Button>}/>
                        <Button variant="destructive" onClick={confirmDeletion}>삭제</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </main>
    );
}
