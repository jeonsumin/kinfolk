# 아키텍처

## 폴더 구조

```
kinfolk-work/workspace/
├── src/
│   ├── app/                          # Next.js App Router
│   │   ├── layout.tsx                # Root layout (font, Sidebar, BottomNav 마운트)
│   │   ├── page.tsx                  # 대시보드 홈 (/) — Server Component
│   │   ├── globals.css               # Tailwind v4 + CSS 변수 + Swiper CSS
│   │   └── components/               # 컴포넌트 라이브러리 데모 (/components)
│   │       ├── page.tsx              # 전체 데모 페이지 — Server Component
│   │       └── carousel-demo.tsx     # Embla·Swiper 데모 — Client Component
│   │
│   └── shared/                       # 전역 공유 모듈 (모든 공통 코드)
│       ├── ui/                       # 공통 UI 컴포넌트 (shadcn 설치 경로)
│       │   ├── index.ts              # 배럴 export — 항상 여기서 import
│       │   ├── avatar.tsx            # Avatar, AvatarImage, AvatarFallback, AvatarGroup
│       │   ├── badge.tsx             # Badge, badgeVariants
│       │   ├── bottom-nav.tsx        # 모바일 하단 네비게이션 (배럴 미포함)
│       │   ├── button.tsx            # Button, buttonVariants
│       │   ├── card.tsx              # Card, CardHeader, CardContent, ImageCard, ImageOverlayCard
│       │   ├── carousel.tsx          # Carousel (Embla/shadcn)
│       │   ├── chip.tsx              # Chip, chipVariants
│       │   ├── empty-state.tsx       # EmptyState
│       │   ├── input.tsx             # Input
│       │   ├── label.tsx             # Label, labelVariants
│       │   ├── list-item.tsx         # ListItem, ListGroup
│       │   ├── month-calendar.tsx    # MonthCalendar (커스텀, 서버 컴포넌트 호환)
│       │   ├── progress.tsx          # Progress, ProgressLabel, ProgressValue
│       │   ├── section-header.tsx    # SectionHeader
│       │   ├── separator.tsx         # Separator
│       │   ├── sidebar.tsx           # 데스크탑 좌측 사이드바 (배럴 미포함)
│       │   ├── skeleton.tsx          # Skeleton
│       │   ├── swiper-carousel.tsx   # SwiperCarousel (커스텀 래퍼)
│       │   ├── switch.tsx            # Switch
│       │   ├── table.tsx             # Table, TableHeader, TableBody, TableRow, ...
│       │   ├── textarea.tsx          # Textarea
│       │   └── top-bar.tsx           # 상단 헤더 (배럴 미포함)
│       ├── config/                   # 앱 전반의 정적 설정값
│       │   ├── app.ts                # APP_NAME, APP_DESCRIPTION, APP_LANG
│       │   ├── nav.ts                # SIDEBAR_NAV, BOTTOM_NAV
│       │   ├── members.ts            # MEMBERS, EXTRA_MEMBER_COUNT, Member 타입
│       │   └── index.ts              # 배럴 export
│       ├── utils/
│       │   └── index.ts              # cn() 유틸 함수
│       ├── form/
│       │   └── index.ts              # react-hook-form 공통 re-export
│       └── store/
│           └── index.ts              # zustand createStore / createPersistedStore 헬퍼
│
├── wiki/                             # LLM 지식 베이스 (이 파일)
├── CLAUDE.md                         # AI 코딩 규칙 (컴포넌트 사용 지침)
├── AGENTS.md                         # Next.js 16 주의사항
├── components.json                   # shadcn 설정 (style: "base-nova")
├── next.config.ts
└── tsconfig.json
```

---

## 핵심 설계 결정

### 1. `shared/ui/` 배럴 export

**규칙**: `@/shared/ui` 배럴 export에서만 import. 개별 파일 경로로 직접 import 금지.

```ts
// ✅ 올바른 방법
import { Button, Card, Label, Switch } from "@/shared/ui"

// ❌ 금지
import { Button } from "@/shared/ui/button"
import { Card } from "../../shared/ui/card"
```

**예외**: 레이아웃 전용 컴포넌트(`Sidebar`, `TopBar`, `BottomNav`)는 배럴에 포함되지 않아 직접 경로 사용.

```ts
import { Sidebar } from "@/shared/ui/sidebar"
import { TopBar } from "@/shared/ui/top-bar"
import { BottomNav } from "@/shared/ui/bottom-nav"
```

### 2. 신규 컴포넌트 추가 절차

1. `src/shared/ui/{name}.tsx` 파일 생성
2. `src/shared/ui/index.ts` 배럴에 export 추가
3. `src/app/components/page.tsx`에 데모 섹션 추가
4. CLAUDE.md 컴포넌트 카탈로그에 문서화

### 3. Server Component / Client Component 경계

| 파일 | 타입 | 이유 |
|------|------|------|
| `app/page.tsx` | Server | 정적 렌더링, 데이터 없음 |
| `app/components/page.tsx` | Server | 정적 demo 페이지 |
| `app/components/carousel-demo.tsx` | Client (`"use client"`) | Embla useState, Swiper 초기화 |
| `shared/ui/sidebar.tsx` | Client | usePathname 훅 |
| `shared/ui/top-bar.tsx` | Client | 날짜 계산 |
| `shared/ui/bottom-nav.tsx` | Client | usePathname 훅 |
| `shared/ui/switch.tsx` | Client | @base-ui/react Switch |
| `shared/ui/progress.tsx` | Client | @base-ui/react Progress |
| `shared/ui/carousel.tsx` | Client | embla-carousel-react |
| `shared/ui/month-calendar.tsx` | Server 호환 | 순수 데이터 계산, 훅 없음 |

**원칙**: Server Component에서 Client Component import는 가능. 반대는 불가.

### 4. 반응형 레이아웃 전략

| 브레이크포인트 | 레이아웃 |
|--------------|---------|
| `< lg` (모바일) | 사이드바 없음, 상단 로고, 하단 네비게이션, FAB 버튼 |
| `≥ lg` (데스크탑) | 좌측 사이드바 (`w-56`), 상단 날짜+날씨, 2열 그리드 |

Root Layout 구조:
```tsx
<body className="h-full flex">
  <Sidebar />           {/* hidden lg:flex w-56 */}
  <div className="flex flex-col flex-1 pb-16 lg:pb-0">
    {children}          {/* TopBar + 페이지 콘텐츠 */}
  </div>
  <BottomNav />         {/* lg:hidden fixed bottom-0 */}
</body>
```

### 5. CSS 아키텍처 (`globals.css`)

Tailwind v4 방식 — `@theme inline {}` 블록으로 CSS 변수를 Tailwind 유틸리티에 매핑.

```css
/* 로드 순서가 중요 */
@import "tailwindcss";
@import "tw-animate-css";
@import "shadcn/tailwind.css";
@import "swiper/css";           /* Tailwind base 레이어보다 먼저 */
@import "swiper/css/pagination";
@import "swiper/css/free-mode";
```

디자인 토큰 변경: `:root {}` 블록만 수정하면 전체 적용.  
Swiper 페이지네이션 dot 스타일은 `globals.css` 하단에 직접 오버라이드로 정의됨 (Tailwind reset 보호 목적).

### 6. shadcn 설정

**style**: `base-nova` → 기반 라이브러리가 `@radix-ui` 대신 `@base-ui/react ^1.6.0`.  
**설치 경로**: `src/shared/ui/` (components.json에 정의됨).  
**새 컴포넌트 추가**: `npx shadcn add <name> --yes` — 기존 커스텀 컴포넌트 덮어쓰기 방지를 위해 프롬프트 주의.

---

## Import 경로 전체 참조

```ts
// UI 컴포넌트 (배럴)
import { Button, Card, Label, Badge, Chip } from "@/shared/ui"
import { Switch, Progress, Table, TableRow } from "@/shared/ui"
import { Avatar, AvatarGroup, AvatarFallback } from "@/shared/ui"
import { MonthCalendar, type CalendarEvent } from "@/shared/ui"
import { Carousel, CarouselContent, CarouselItem } from "@/shared/ui"
import { SwiperCarousel } from "@/shared/ui"
import { EmptyState, Skeleton, Separator } from "@/shared/ui"
import { ListItem, ListGroup, SectionHeader } from "@/shared/ui"
import { Input, Textarea, Label, labelVariants } from "@/shared/ui"

// 레이아웃 컴포넌트 (개별 경로)
import { Sidebar } from "@/shared/ui/sidebar"
import { TopBar } from "@/shared/ui/top-bar"
import { BottomNav } from "@/shared/ui/bottom-nav"

// 유틸리티
import { cn } from "@/shared/utils"

// 설정값
import { APP_NAME, SIDEBAR_NAV, MEMBERS } from "@/shared/config"

// 상태관리
import { createStore, createPersistedStore } from "@/shared/store"

// 폼
import { useForm, FormProvider, type SubmitHandler } from "@/shared/form"
```
