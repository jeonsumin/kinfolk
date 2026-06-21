import { TopBar } from "@/shared/ui/top-bar";
import { Plus, Check, ChevronRight, ShoppingCart, Clock } from "lucide-react";
import { MEMBERS, EXTRA_MEMBER_COUNT } from "@/shared/config";

const scheduleItems = [
  {
    time: "08:00",
    title: "등교 도와주기",
    subtitle: "킹컨 초등학교",
    tag: "교육",
    tagColor: "bg-blue-100 text-blue-700",
    done: false,
    accent: "#b9c7df",
  },
  {
    time: "12:30",
    title: "할머니와 점심 식사",
    subtitle: "더코지 스푼",
    tag: "가족",
    tagColor: "bg-emerald-100 text-emerald-700",
    done: false,
    accent: "#475569",
  },
  {
    time: "14:00",
    title: "주간 가족 회의",
    subtitle: "",
    tag: "업무",
    tagColor: "bg-violet-100 text-violet-700",
    done: true,
    accent: "#c4c6cd",
  },
];

const shoppingItems = [
  { label: "두부", done: true },
  { label: "당근", done: true },
  { label: "달걀 (30개)", done: false },
  { label: "우유", done: false },
  { label: "세제", done: false },
];

const weatherDays = [
  { day: "오늘", icon: "☀️", high: 24, low: 17 },
  { day: "내일", icon: "⛅", high: 21, low: 15 },
  { day: "모레", icon: "🌧️", high: 18, low: 13 },
];

const familyPhotos = [
  { alt: "가족 저녁", bg: "#d5e3fc" },
  { alt: "고양이", bg: "#c4d4ec" },
  { alt: "반려동물", bg: "#b9c7df" },
  { alt: "야외 활동", bg: "#d4e4fa" },
];

export default function DashboardPage() {
  const doneCount = shoppingItems.filter((i) => i.done).length;

  return (
    <div className="flex flex-col flex-1 h-full overflow-hidden">
      <TopBar />

      <div className="flex-1 overflow-y-auto">
        <div className="max-w-6xl mx-auto px-4 lg:px-6 py-5 lg:py-6">

          {/* ── Greeting ── */}
          <div className="mb-5 lg:mb-6">
            <h2 className="text-2xl lg:text-2xl font-bold text-foreground tracking-tight leading-tight">
              좋은 아침이에요, 이주님!
            </h2>
            <p className="text-sm text-muted-foreground mt-0.5">화요일입니다.</p>
          </div>

          {/* ── Mobile-only: weather + avatars ── */}
          <div className="lg:hidden mb-5 space-y-3">
            {/* Weather row */}
            <div className="flex items-center gap-3">
              <span className="text-2xl">☀️</span>
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-lg font-semibold text-foreground">22°C</span>
                  <span className="text-sm text-muted-foreground">맑음</span>
                </div>
                <div className="flex items-center gap-1 mt-0.5">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 inline-block" />
                  <span className="text-xs text-muted-foreground">미세먼지 좋음</span>
                </div>
              </div>
            </div>

            {/* Family avatars */}
            <div className="flex items-center gap-1.5">
              {MEMBERS.map((m, i) => (
                <div
                  key={i}
                  className="w-10 h-10 rounded-full flex items-center justify-center text-sm font-semibold text-white ring-2 ring-background"
                  style={{ backgroundColor: m.color }}
                >
                  {m.initials}
                </div>
              ))}
              <div className="w-10 h-10 rounded-full flex items-center justify-center text-xs font-semibold text-muted-foreground bg-muted ring-2 ring-background">
                +{EXTRA_MEMBER_COUNT}
              </div>
            </div>
          </div>

          {/* ── Main grid ── */}
          <div className="grid grid-cols-1 lg:grid-cols-[1fr_280px] gap-4 lg:gap-5">

            {/* Left column */}
            <div className="flex flex-col gap-4 lg:gap-5">

              {/* Today's Schedule */}
              <section className="bg-card rounded-xl border border-border p-4 lg:p-5">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <Clock size={14} className="text-muted-foreground" strokeWidth={1.8} />
                    <h3 className="text-sm font-semibold text-foreground">오늘의 일정</h3>
                  </div>
                  <button className="flex items-center gap-0.5 text-xs text-muted-foreground hover:text-foreground transition-colors">
                    전체 보기 <ChevronRight size={13} />
                  </button>
                </div>

                <div className="space-y-0">
                  {scheduleItems.map((item, i) => (
                    <div key={i} className="flex items-stretch gap-3 py-3 border-b border-border last:border-0">
                      {/* Time */}
                      <span className="text-xs text-muted-foreground w-10 shrink-0 font-medium tabular-nums pt-0.5">
                        {item.time}
                      </span>

                      {/* Left accent */}
                      <div
                        className="w-0.5 rounded-full shrink-0 self-stretch min-h-[40px]"
                        style={{ backgroundColor: item.accent }}
                      />

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <p
                          className={`text-sm font-medium leading-snug ${
                            item.done ? "line-through text-muted-foreground" : "text-foreground"
                          }`}
                        >
                          {item.title}
                        </p>
                        {item.subtitle && (
                          <p className="text-xs text-muted-foreground mt-0.5 flex items-center gap-1">
                            <span>📍</span>
                            {item.subtitle}
                          </p>
                        )}
                      </div>

                      {/* Tag — desktop only */}
                      <span
                        className={`hidden lg:inline-flex items-center shrink-0 text-xs px-2 py-0.5 rounded-full font-medium h-fit mt-0.5 ${item.tagColor}`}
                      >
                        {item.tag}
                      </span>
                    </div>
                  ))}
                </div>

                <button className="mt-3 w-full flex items-center justify-center gap-2 py-2 rounded-lg border border-dashed border-border text-xs text-muted-foreground hover:border-primary hover:text-primary transition-colors">
                  <Plus size={13} strokeWidth={2.5} />
                  일정 추가
                </button>
              </section>

              {/* Family Photos */}
              <section className="bg-card rounded-xl border border-border p-4 lg:p-5">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-sm font-semibold text-foreground">가족 사진 공유</h3>
                  <button className="flex items-center gap-0.5 text-xs text-muted-foreground hover:text-foreground transition-colors">
                    갤러리 보기 <ChevronRight size={13} />
                  </button>
                </div>
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-2 lg:gap-3">
                  {familyPhotos.map((photo, i) => (
                    <div
                      key={i}
                      className="aspect-square rounded-lg overflow-hidden"
                      style={{ backgroundColor: photo.bg }}
                    >
                      <div className="w-full h-full flex items-center justify-center text-xs text-muted-foreground opacity-50 p-2 text-center">
                        {photo.alt}
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            </div>

            {/* Right column — stacks below on mobile */}
            <div className="flex flex-col gap-4">

              {/* Quick add — desktop only */}
              <section className="hidden lg:block bg-card rounded-xl border border-border p-4">
                <h3 className="text-sm font-semibold text-foreground mb-3">빠른 추가</h3>
                <button className="w-full flex items-center justify-center gap-2 py-2.5 rounded-lg bg-primary text-primary-foreground text-xs font-semibold hover:bg-primary/90 transition-colors">
                  <Plus size={14} strokeWidth={2.5} />
                  새 항목 추가
                </button>
              </section>

              {/* Shopping list */}
              <section className="bg-card rounded-xl border border-border p-4">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2">
                    <ShoppingCart size={14} className="text-muted-foreground" strokeWidth={1.8} />
                    <h3 className="text-sm font-semibold text-foreground">쇼핑 리스트</h3>
                  </div>
                  <span className="text-xs text-muted-foreground">
                    {doneCount}/{shoppingItems.length}
                  </span>
                </div>

                <div className="h-1 rounded-full bg-muted mb-3 overflow-hidden">
                  <div
                    className="h-full rounded-full bg-primary transition-all"
                    style={{ width: `${(doneCount / shoppingItems.length) * 100}%` }}
                  />
                </div>

                <ul className="space-y-2">
                  {shoppingItems.map((item, i) => (
                    <li key={i} className="flex items-center gap-2.5">
                      <div
                        className={`w-4 h-4 rounded flex items-center justify-center shrink-0 border transition-colors ${
                          item.done ? "bg-primary border-primary" : "border-border"
                        }`}
                      >
                        {item.done && (
                          <Check size={10} strokeWidth={3} className="text-white" />
                        )}
                      </div>
                      <span
                        className={`text-xs ${
                          item.done ? "line-through text-muted-foreground" : "text-foreground"
                        }`}
                      >
                        {item.label}
                      </span>
                    </li>
                  ))}
                </ul>
              </section>

              {/* Weather */}
              <section className="bg-card rounded-xl border border-border p-4">
                <h3 className="text-sm font-semibold text-foreground mb-3">다가오는 날씨</h3>
                <div className="space-y-2.5">
                  {weatherDays.map((day, i) => (
                    <div key={i} className="flex items-center justify-between">
                      <span className="text-xs text-muted-foreground w-10">{day.day}</span>
                      <span className="text-base">{day.icon}</span>
                      <div className="flex items-center gap-2 text-xs">
                        <span className="font-medium text-foreground">{day.high}°</span>
                        <span className="text-muted-foreground">{day.low}°</span>
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            </div>
          </div>
        </div>
      </div>

      {/* Mobile FAB */}
      <button
        className="lg:hidden fixed bottom-20 right-5 w-14 h-14 rounded-full bg-foreground text-background flex items-center justify-center shadow-xl hover:bg-foreground/90 transition-colors z-40"
        aria-label="추가"
      >
        <Plus size={24} strokeWidth={2.5} />
      </button>
    </div>
  );
}
