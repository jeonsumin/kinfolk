import {
  Clock,
  MapPin,
  Utensils,
  ShoppingCart,
  Heart,
  Cake,
  PartyPopper,
  Target,
  Send,
  Camera,
  CloudSun,
  Plus,
  Images,
  Goal,
} from "lucide-react";
import { TopBar } from "@/shared/ui/top-bar";
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
} from "@/shared/ui";
import { MEMBERS, EXTRA_MEMBER_COUNT } from "@/shared/config";
import { cn } from "@/shared/utils";

/* ── Mock data (UI 전용 — 비즈니스 로직은 frontend-agent 위임) ── */
type TagColor = "green" | "slate" | "amber";

const TIMELINE = [
  {
    time: "08:00",
    title: "등교 도와주기",
    place: "링컨 초등학교",
    placeIcon: MapPin,
    tag: "아이들",
    tagColor: "green" as TagColor,
    accent: "#516072",
    active: false,
  },
  {
    time: "12:30",
    title: "할머니와 점심 식사",
    place: "더 코지 스푼",
    placeIcon: Utensils,
    tag: "가족",
    tagColor: "slate" as TagColor,
    accent: "#475569",
    active: false,
  },
  {
    time: "15:00",
    title: "축구 연습",
    place: "서쪽 경기장",
    placeIcon: Goal,
    tag: "취미",
    tagColor: "amber" as TagColor,
    accent: "#303e51",
    active: true,
  },
];

const QUICK_TAGS = ["🥕 장보기", "🧼 집안일", "🎉 기념일"];

const SHOPPING = [
  { label: "우유 사기", done: false },
  { label: "사과 5개", done: false },
  { label: "세제", done: false },
];

const UPCOMING = [
  { icon: Cake, title: "미아의 8번째 생일", meta: "3일 남음 • 10월 27일" },
  { icon: PartyPopper, title: "부모님 결혼기념일", meta: "12일 남음 • 11월 5일" },
];

const CHORES = [
  { label: "주방 청소하기", value: 80 },
  { label: "뒷마당 가꾸기", value: 35 },
];

const PHOTOS = [
  { title: "밀러 호수 소풍", date: "2024.10.20", gradient: "from-[#b9c7df] to-[#475569]" },
  { title: "주말 아침 식사", date: "2024.10.19", gradient: "from-[#d4e4fa] to-[#516072]" },
  { title: "미아의 그림 교실", date: "2024.10.15", gradient: "from-[#bbcae1] to-[#343e47]" },
  { title: "공원 산책", date: "2024.10.12", gradient: "from-[#c4d4ec] to-[#303e51]" },
];

export default function DashboardPage() {
  return (
    <div className="flex flex-col flex-1 h-full overflow-hidden">
      <TopBar />

      <div className="flex-1 overflow-y-auto">
        <div className="max-w-5xl mx-auto px-4 lg:px-6 py-5 lg:py-6">
          {/* ── Welcome header ── */}
          <header className="mb-6 flex flex-col md:flex-row md:items-end justify-between gap-4">
            <div className="flex-1 flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div className="space-y-1">
                <h2 className="text-2xl lg:text-[32px] font-semibold text-foreground tracking-tight leading-tight">
                  좋은 아침이에요, 이주님!
                </h2>
                <p className="text-sm text-muted-foreground">화요일입니다.</p>
              </div>

              {/* Weather */}
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-1">
                  <CloudSun size={26} className="text-primary" strokeWidth={1.8} />
                  <span className="text-2xl font-extrabold text-primary">22°C</span>
                </div>
                <div className="flex flex-col gap-0.5">
                  <span className="text-sm font-bold text-foreground leading-tight">맑음</span>
                  <Chip color="blue" size="sm" className="font-extrabold uppercase tracking-wider">
                    <span className="w-1.5 h-1.5 rounded-full bg-secondary-foreground" />
                    미세먼지 좋음
                  </Chip>
                </div>
              </div>
            </div>

            {/* Family avatars */}
            <AvatarGroup>
              {MEMBERS.map((m) => (
                <Avatar key={m.name} size="lg">
                  <AvatarFallback
                    className="text-sm font-semibold text-white"
                    style={{ backgroundColor: m.color }}
                  >
                    {m.initials}
                  </AvatarFallback>
                </Avatar>
              ))}
              <AvatarGroupCount>+{EXTRA_MEMBER_COUNT}</AvatarGroupCount>
            </AvatarGroup>
          </header>

          {/* ── Bento grid ── */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
            {/* Today timeline */}
            <section className="lg:col-span-8 bg-card rounded-xl p-5 lg:p-6 border border-border shadow-sm">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
                  <Clock size={18} className="text-muted-foreground" strokeWidth={1.8} />
                  오늘의 일정
                </h3>
                <button className="text-primary font-bold text-sm hover:underline">
                  캘린더 보기
                </button>
              </div>

              <div className="relative space-y-5">
                {/* Vertical line */}
                <div className="absolute left-6 top-2 bottom-2 w-px border-l border-dashed border-border" />

                {TIMELINE.map((event) => {
                  const PlaceIcon = event.placeIcon;
                  return (
                    <div key={event.title} className="flex gap-4 relative z-10">
                      <div className="w-12 flex flex-col items-center shrink-0">
                        <span
                          className={cn(
                            "text-xs font-bold mb-1",
                            event.active ? "text-primary" : "text-muted-foreground"
                          )}
                        >
                          {event.time}
                        </span>
                        <span
                          className="w-4 h-4 rounded-full ring-4 ring-card"
                          style={{ backgroundColor: event.accent }}
                        />
                      </div>

                      <div
                        className={cn(
                          "flex-1 rounded-xl p-4 border-l-4 transition-shadow cursor-pointer",
                          event.active
                            ? "bg-card ring-1 ring-border shadow-md"
                            : "bg-muted/50"
                        )}
                        style={{ borderLeftColor: event.accent }}
                      >
                        <div className="flex justify-between items-start gap-2">
                          <div>
                            {event.active && (
                              <div className="flex items-center gap-2 mb-1">
                                <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                                <span className="text-[10px] font-bold text-primary uppercase">
                                  지금 진행 중
                                </span>
                              </div>
                            )}
                            <h4 className="font-bold text-foreground">{event.title}</h4>
                            <p className="text-sm text-muted-foreground flex items-center gap-1 mt-0.5">
                              <PlaceIcon size={14} strokeWidth={1.8} />
                              {event.place}
                            </p>
                          </div>
                          <Chip color={event.tagColor} size="sm" className="font-extrabold uppercase tracking-wider shrink-0">
                            {event.tag}
                          </Chip>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </section>

            {/* Right widgets */}
            <div className="lg:col-span-4 flex flex-col gap-5">
              {/* Quick add */}
              <section className="bg-primary text-primary-foreground rounded-xl p-5 shadow-sm flex flex-col gap-4">
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
                      <Send size={16} strokeWidth={2} />
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

              {/* Shopping list */}
              <section className="bg-card rounded-xl p-5 border border-border shadow-sm">
                <div className="flex items-center gap-2 mb-4">
                  <ShoppingCart size={18} className="text-muted-foreground" strokeWidth={1.8} />
                  <h3 className="font-bold text-foreground">장보기 리스트</h3>
                </div>
                <div className="space-y-3">
                  {SHOPPING.map((item) => (
                    <label key={item.label} className="flex items-center gap-3 cursor-pointer">
                      <Checkbox defaultChecked={item.done} />
                      <span className="text-sm text-foreground">{item.label}</span>
                    </label>
                  ))}
                </div>
                <Button variant="ghost" className="w-full mt-4 bg-muted/60 hover:bg-muted text-primary font-bold">
                  전체 보기
                </Button>
              </section>

              {/* Upcoming events */}
              <section className="bg-card rounded-xl p-5 border border-border shadow-sm">
                <div className="flex items-center gap-2 mb-4">
                  <Heart size={18} className="text-muted-foreground" strokeWidth={1.8} />
                  <h3 className="font-bold text-foreground">다가오는 일정</h3>
                </div>
                <div className="space-y-4">
                  {UPCOMING.map((event) => {
                    const Icon = event.icon;
                    return (
                      <div key={event.title} className="flex items-center gap-4 group cursor-pointer">
                        <div className="w-12 h-12 bg-muted text-muted-foreground rounded-lg flex items-center justify-center shrink-0 group-hover:bg-primary group-hover:text-primary-foreground transition-colors">
                          <Icon size={22} strokeWidth={1.8} />
                        </div>
                        <div>
                          <h4 className="font-bold text-sm text-foreground">{event.title}</h4>
                          <p className="text-xs text-muted-foreground">{event.meta}</p>
                        </div>
                      </div>
                    );
                  })}
                </div>
                <Button variant="ghost" className="w-full mt-6 bg-muted/60 hover:bg-muted text-primary font-bold">
                  메모리 박스 열기
                </Button>
              </section>

              {/* Chores progress */}
              <section className="bg-muted/50 rounded-xl p-5 border border-border">
                <h3 className="font-bold text-foreground mb-2 flex items-center gap-2">
                  <Target size={18} className="text-muted-foreground" strokeWidth={1.8} />
                  가족 공동 목표
                </h3>
                <div className="space-y-4 mt-4">
                  {CHORES.map((chore) => (
                    <Progress key={chore.label} value={chore.value}>
                      <ProgressLabel className="text-xs font-bold text-muted-foreground">
                        {chore.label}
                      </ProgressLabel>
                      <ProgressValue className="text-xs font-bold text-muted-foreground" />
                    </Progress>
                  ))}
                </div>
              </section>
            </div>
          </div>

          {/* ── Family photo panorama ── */}
          <section className="mt-5 bg-card rounded-xl p-5 lg:p-6 border border-border shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
                <Images size={18} className="text-muted-foreground" strokeWidth={1.8} />
                가족 사진 공유
              </h3>
              <button className="text-primary font-bold text-sm hover:underline">
                갤러리 전체보기
              </button>
            </div>

            <div className="flex gap-4 overflow-x-auto pb-2 snap-x snap-mandatory [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
              {PHOTOS.map((photo) => (
                <ImageOverlayCard
                  key={photo.title}
                  title={photo.title}
                  subtitle={photo.date}
                  aspectRatio="aspect-[3/2]"
                  className="w-72 shrink-0 snap-start"
                >
                  <div className={cn("h-full w-full bg-gradient-to-br", photo.gradient)} />
                </ImageOverlayCard>
              ))}

              {/* Add photo */}
              <button className="w-48 shrink-0 snap-start aspect-[3/2] rounded-xl bg-muted border-2 border-dashed border-border flex flex-col items-center justify-center text-muted-foreground hover:bg-accent transition-colors">
                <Camera size={32} strokeWidth={1.6} className="mb-2" />
                <span className="text-sm font-bold">사진 추가</span>
              </button>
            </div>
          </section>
        </div>
      </div>

      {/* FAB */}
      <button
        className="fixed bottom-20 right-5 lg:bottom-8 lg:right-8 w-14 h-14 rounded-xl bg-primary text-primary-foreground flex items-center justify-center shadow-xl hover:bg-primary/90 active:scale-95 transition-all z-40"
        aria-label="추가"
      >
        <Plus size={24} strokeWidth={2.5} />
      </button>
    </div>
  );
}
