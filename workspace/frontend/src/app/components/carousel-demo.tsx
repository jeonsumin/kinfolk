"use client"

import {
  Avatar,
  AvatarFallback,
  Badge,
  Card,
  CardContent,
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
  Chip,
  SwiperCarousel,
} from "@/shared/ui"

// ─── 공통 슬라이드 데이터 ───────────────────────────────────
const photoSlides = [
  { bg: "from-[#b9c7df] to-[#475569]", label: "제주도 여름 여행", date: "2025.07.12", member: "이주" },
  { bg: "from-[#d1f5e4] to-[#2e7d5a]", label: "할머니 생신 파티", date: "2025.06.01", member: "김민" },
  { bg: "from-[#ead6f0] to-[#7c4d8a]", label: "공원 피크닉", date: "2025.05.18", member: "박선" },
  { bg: "from-[#fdf0d0] to-[#8a6800]", label: "가족 운동회", date: "2025.04.22", member: "최연" },
]

const memberSlides = [
  { name: "이주", role: "엄마", color: "bg-[#b9c7df]", chips: ["일정 관리", "요리"] },
  { name: "김민", role: "아빠", color: "bg-[#c4d4ec]", chips: ["운동", "드라이브"] },
  { name: "박선", role: "첫째", color: "bg-[#d4e4fa]", chips: ["독서", "음악"] },
  { name: "최연", role: "둘째", color: "bg-[#dae3f0]", chips: ["그림", "게임"] },
  { name: "이강", role: "막내", color: "bg-[#e0e3e5]", chips: ["블록", "낮잠"] },
]

// ─── Slide card components (shared between embla & swiper) ──
function PhotoSlideCard({ slide, index, total }: {
  slide: typeof photoSlides[number]
  index: number
  total: number
}) {
  return (
    <Card className="pt-0 overflow-hidden">
      <div className={`h-48 bg-gradient-to-br ${slide.bg} relative flex items-end px-5 pb-4`}>
        <div className="flex items-center gap-2.5">
          <Avatar size="sm">
            <AvatarFallback>{slide.member}</AvatarFallback>
          </Avatar>
          <div>
            <p className="text-xs font-semibold text-white leading-snug">{slide.label}</p>
            <p className="text-[11px] text-white/70">{slide.date} · {slide.member}</p>
          </div>
        </div>
        <Badge variant="secondary" className="absolute top-3 right-3">
          {index + 1} / {total}
        </Badge>
      </div>
      <CardContent className="pt-4">
        <p className="text-sm text-muted-foreground">가족과 함께한 소중한 순간을 기록했습니다.</p>
      </CardContent>
    </Card>
  )
}

function MemberSlideCard({ member }: { member: typeof memberSlides[number] }) {
  return (
    <Card size="sm" className="text-center">
      <CardContent className="flex flex-col items-center gap-3 pt-5 pb-4">
        <div className={`size-14 rounded-full ${member.color} flex items-center justify-center`}>
          <span className="text-sm font-semibold text-foreground/70">{member.name}</span>
        </div>
        <div>
          <p className="text-sm font-semibold text-foreground">{member.name}</p>
          <p className="text-xs text-muted-foreground">{member.role}</p>
        </div>
        <div className="flex flex-wrap justify-center gap-1">
          {member.chips.map((chip) => (
            <Chip key={chip} color="blue" size="sm">{chip}</Chip>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}

// ─── Embla (shadcn) Carousel demos ──────────────────────────
export function EmblaCarouselDemo() {
  return (
    <div className="space-y-4">
      {/* Photo carousel */}
      <div className="space-y-2">
        <p className="text-[11px] font-medium text-muted-foreground/70 uppercase tracking-wider">
          Photo Carousel (loop)
        </p>
        <div className="rounded-xl border border-border bg-card p-5">
          <Carousel opts={{ loop: true }} className="w-full">
            <CarouselContent>
              {photoSlides.map((slide, i) => (
                <CarouselItem key={i}>
                  <PhotoSlideCard slide={slide} index={i} total={photoSlides.length} />
                </CarouselItem>
              ))}
            </CarouselContent>
            {/* 버튼을 캐러셀 안쪽(오버레이)에 배치해 클리핑 방지 */}
            <CarouselPrevious className="left-2 bg-background/80 backdrop-blur-sm" />
            <CarouselNext className="right-2 bg-background/80 backdrop-blur-sm" />
          </Carousel>
        </div>
      </div>

      {/* Member card carousel */}
      <div className="space-y-2">
        <p className="text-[11px] font-medium text-muted-foreground/70 uppercase tracking-wider">
          Member Cards (multi-slide)
        </p>
        <div className="rounded-xl border border-border bg-card p-5">
          <Carousel opts={{ align: "start" }} className="w-full">
            <CarouselContent className="-ml-3">
              {memberSlides.map((member, i) => (
                <CarouselItem key={i} className="pl-3 basis-1/2 md:basis-1/3">
                  <MemberSlideCard member={member} />
                </CarouselItem>
              ))}
            </CarouselContent>
            <CarouselPrevious className="left-2 bg-background/80 backdrop-blur-sm" />
            <CarouselNext className="right-2 bg-background/80 backdrop-blur-sm" />
          </Carousel>
        </div>
      </div>
    </div>
  )
}

// ─── Swiper Carousel demos ───────────────────────────────────
export function SwiperCarouselDemo() {
  return (
    <div className="space-y-4">
      {/* Swiper photo carousel with pagination */}
      <div className="space-y-2">
        <p className="text-[11px] font-medium text-muted-foreground/70 uppercase tracking-wider">
          Swiper — Pagination + Autoplay
        </p>
        <div className="rounded-xl border border-border bg-card p-5">
          <SwiperCarousel
            showPagination
            autoplay={{ delay: 3000, disableOnInteraction: false }}
            loop
            spaceBetween={16}
          >
            {photoSlides.map((slide, i) => (
              <PhotoSlideCard key={i} slide={slide} index={i} total={photoSlides.length} />
            ))}
          </SwiperCarousel>
        </div>
      </div>

      {/* Swiper member cards with free mode */}
      <div className="space-y-2">
        <p className="text-[11px] font-medium text-muted-foreground/70 uppercase tracking-wider">
          Swiper — Free Mode (drag-scroll)
        </p>
        <div className="rounded-xl border border-border bg-card p-5">
          <SwiperCarousel
            freeMode
            slidesPerView={2.3}
            spaceBetween={12}
            breakpoints={{
              640: { slidesPerView: 3.3 },
              1024: { slidesPerView: 4.2 },
            }}
          >
            {memberSlides.map((member, i) => (
              <MemberSlideCard key={i} member={member} />
            ))}
          </SwiperCarousel>
        </div>
      </div>
    </div>
  )
}
