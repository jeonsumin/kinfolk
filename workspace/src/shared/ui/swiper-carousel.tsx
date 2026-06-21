"use client"

import * as React from "react"
import { Swiper, SwiperSlide } from "swiper/react"
import { Pagination, Autoplay, FreeMode } from "swiper/modules"

import { cn } from "@/shared/utils"

// 모듈을 컴포넌트 밖 상수로 정의 — 렌더마다 새 배열이 생기면 Swiper가 재초기화됨
const SWIPER_MODULES = [Pagination, Autoplay, FreeMode]

interface SwiperCarouselProps {
  children: React.ReactNode
  slidesPerView?: number | "auto"
  spaceBetween?: number
  loop?: boolean
  showPagination?: boolean
  freeMode?: boolean
  autoplay?: boolean | { delay?: number; disableOnInteraction?: boolean }
  breakpoints?: Record<number, { slidesPerView?: number | "auto"; spaceBetween?: number }>
  className?: string
  slideClassName?: string
}

function SwiperCarousel({
  children,
  slidesPerView = 1,
  spaceBetween = 0,
  loop = false,
  showPagination = false,
  freeMode = false,
  autoplay = false,
  breakpoints,
  className,
  slideClassName,
}: SwiperCarouselProps) {
  const autoplayConfig = React.useMemo(() => {
    if (!autoplay) return false
    if (autoplay === true) return { delay: 3000, disableOnInteraction: false }
    return autoplay
  }, [autoplay])

  return (
    <div className={cn("w-full", className)}>
      <Swiper
        modules={SWIPER_MODULES}
        slidesPerView={slidesPerView}
        spaceBetween={spaceBetween}
        loop={loop}
        pagination={showPagination ? { clickable: true } : false}
        freeMode={freeMode}
        autoplay={autoplayConfig as never}
        breakpoints={breakpoints}
        style={{ width: "100%", height: "100%" }}
      >
        {React.Children.map(children, (child, i) => (
          <SwiperSlide key={i} className={slideClassName}>
            {child}
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  )
}

export { SwiperCarousel }
