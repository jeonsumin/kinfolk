# 컴포넌트 목록

## Import 규칙

모든 공통 UI 컴포넌트는 **반드시 배럴 export**에서 import한다.

```ts
// ✅ 올바른 방법
import { Button, Card, Label, Switch } from "@/shared/ui"

// ❌ 금지
import { Button } from "@/shared/ui/button"
```

레이아웃 컴포넌트(`Sidebar`, `TopBar`, `BottomNav`)만 예외적으로 개별 파일에서 import한다 (배럴에 미포함).

---

## 레이아웃 컴포넌트 (배럴 미포함)

### `<Sidebar />`
**파일**: `src/shared/ui/sidebar.tsx` · Client Component  
**표시**: `hidden lg:flex` — 데스크탑(lg+)만

- 로고(Kinfolk / Table), 네비게이션 링크 목록, 하단 추가 버튼으로 구성
- 데이터: `SIDEBAR_NAV` from `@/shared/config` (8개 항목)
- 활성 링크: `usePathname() === href`

### `<TopBar />`
**파일**: `src/shared/ui/top-bar.tsx` · Client Component

| | 모바일 | 데스크탑 |
|--|--------|---------|
| 좌 | APP_NAME | 현재 날짜 (ko-KR) |
| 우 | 검색(아이콘) + 벨 + 아바타 | 날씨 + 검색(텍스트) + 아바타 |

### `<BottomNav />`
**파일**: `src/shared/ui/bottom-nav.tsx` · Client Component  
**표시**: `lg:hidden fixed bottom-0 z-50` — 모바일만  
데이터: `BOTTOM_NAV` from `@/shared/config` (3개: 홈/캘린더/사진), 중앙 FAB 버튼 포함

---

## 공통 UI 컴포넌트 (`@/shared/ui`)

### 기본 액션

#### `Button` · `buttonVariants`
```tsx
<Button
  variant="default" | "outline" | "secondary" | "ghost" | "destructive" | "link"
  size="xs" | "sm" | "default" | "lg" | "icon" | "icon-xs" | "icon-sm" | "icon-lg"
>
```
`buttonVariants` 함수도 export됨 — 외부 className 조합에 활용.

#### `Switch`
ON/OFF 토글. 멤버 가시성, 알림 설정 등.
```tsx
<Switch size="sm" | "default"  defaultChecked={boolean}  disabled={boolean} />
```

---

### 텍스트 & 레이블

#### `Label` · `labelVariants`
폼 필드, 섹션 레이블. `<p>` · `<span>`으로 대체 금지.
```tsx
<Label
  variant="default" | "muted" | "destructive" | "required"
  size="sm" | "default" | "lg"
  htmlFor="input-id"
>
```
- `required` → 자동으로 `*` 표시 추가
- `labelVariants` 함수도 export됨

#### `Badge` · `badgeVariants`
상태, 카운트, 태그 등 소형 강조.
```tsx
<Badge variant="default" | "secondary" | "destructive" | "outline">완료</Badge>
```

#### `Chip` · `chipVariants`
카테고리, 태그, 필터. 파스텔 컬러 시스템.
```tsx
<Chip
  color="default" | "slate" | "blue" | "green" | "mauve" | "amber"
  size="sm" | "md" | "lg"
  icon={<Icon />}
>
```

| color | bg | text |
|-------|----|------|
| blue  | `#d2e1f7` | `#516072` |
| green | `#d1f5e4` | `#2e7d5a` |
| mauve | `#ead6f0` | `#7c4d8a` |
| amber | `#fdf0d0` | `#8a6800` |

---

### 카드

#### `Card` + `CardHeader` · `CardTitle` · `CardDescription` · `CardContent` · `CardFooter` · `CardAction`
콘텐츠 컨테이너 기본 단위.
```tsx
<Card size="default" | "sm">
  <CardHeader>
    <CardTitle>제목</CardTitle>
    <CardDescription>설명</CardDescription>
    <CardAction>버튼</CardAction>
  </CardHeader>
  <CardContent>내용</CardContent>
  <CardFooter>하단</CardFooter>
</Card>
```

#### `ImageCard`
이미지만 표시하는 카드. `src` 없이 `children`으로 배경 지정 가능.
```tsx
<ImageCard src="/photo.jpg" alt="사진" aspectRatio="aspect-[4/3]" />

// src 없이 children으로 플레이스홀더
<ImageCard aspectRatio="aspect-video">
  <div className="h-full w-full bg-gradient-to-br from-[#b9c7df] to-[#475569]" />
</ImageCard>
```

#### `ImageOverlayCard`
전체 이미지 + 왼쪽 하단 title/subtitle 오버레이. `src` 없을 때 `children`이 배경.
```tsx
<ImageOverlayCard
  title="제주도 여행"
  subtitle="2025.07.12 · 가족 5명"
  src="/photo.jpg"
  aspectRatio="aspect-[4/3]"
/>
```

---

### 폼 요소

#### `Input`
```tsx
<Input placeholder="홍길동" aria-invalid defaultValue="잘못된 값" />
```

#### `Textarea`
```tsx
<Textarea placeholder="메시지..." rows={5} />
```

---

### 피드백 & 상태

#### `Progress` + `ProgressLabel` · `ProgressValue` · `ProgressTrack` · `ProgressIndicator`
```tsx
// 기본
<Progress value={75} />

// 레이블 포함
<Progress value={75}>
  <ProgressLabel>정원 청소</ProgressLabel>
  <ProgressValue />  {/* "75%" 자동 렌더 */}
</Progress>
```

#### `Skeleton`
로딩 플레이스홀더. 실제 콘텐츠와 동일한 크기로 지정.
```tsx
<Skeleton className="h-4 w-3/4" />
<Skeleton className="size-8 rounded-full" />
```

#### `EmptyState`
```tsx
<EmptyState
  icon={<InboxIcon />}
  title="일정이 없습니다"
  description="새로운 일정을 추가해 보세요."
  action={<Button size="sm">추가</Button>}
/>
```

---

### 레이아웃 & 구조

#### `SectionHeader`
```tsx
<SectionHeader
  title="오늘의 일정"
  subtitle="3개 일정 예정"
  action={<Button size="sm">추가</Button>}
/>
```

#### `Separator`
```tsx
<Separator />
<Separator orientation="vertical" />
```

#### `ListItem` + `ListGroup`
`onClick` prop 있으면 hover 스타일 자동 적용.
```tsx
<ListGroup>
  <ListItem
    leading={<Icon />}
    title="멤버"
    subtitle="5명"
    trailing={<Badge>5</Badge>}
    onClick={() => {}}
  />
</ListGroup>
```

---

### 미디어 & 사용자

#### `Avatar` + `AvatarImage` · `AvatarFallback` · `AvatarBadge`
```tsx
<Avatar size="sm" | "default" | "lg">
  <AvatarImage src="/user.jpg" alt="이주" />
  <AvatarFallback>이주</AvatarFallback>
</Avatar>
```

#### `AvatarGroup` + `AvatarGroupCount`
```tsx
<AvatarGroup>
  <Avatar><AvatarFallback>이주</AvatarFallback></Avatar>
  <Avatar><AvatarFallback>김민</AvatarFallback></Avatar>
  <AvatarGroupCount>+3</AvatarGroupCount>
</AvatarGroup>
```

---

### 데이터 & 표

#### `Table` + `TableHeader` · `TableBody` · `TableRow` · `TableHead` · `TableCell` · `TableFooter` · `TableCaption`
```tsx
<Table>
  <TableHeader>
    <TableRow>
      <TableHead>항목</TableHead>
      <TableHead className="text-right">금액</TableHead>
    </TableRow>
  </TableHeader>
  <TableBody>
    <TableRow>
      <TableCell>항공권</TableCell>
      <TableCell className="text-right tabular-nums">₩600,000</TableCell>
    </TableRow>
  </TableBody>
</Table>
```

---

### 캐러셀

#### `Carousel` (Embla/shadcn) + `CarouselContent` · `CarouselItem` · `CarouselNext` · `CarouselPrevious`
- 내부 `useCarousel` hook과 `CarouselApi` 타입도 export됨
- 버튼은 캐러셀 내부에 overlay로 배치 (`left-2` / `right-2`) — 외부(`-left-3`)로 두면 부모 overflow에 잘림

```tsx
<Carousel opts={{ loop: true, align: "start" }}>
  <CarouselContent className="-ml-3">
    <CarouselItem className="pl-3 basis-1/2">슬라이드</CarouselItem>
  </CarouselContent>
  <CarouselPrevious className="left-2 bg-background/80 backdrop-blur-sm" />
  <CarouselNext className="right-2 bg-background/80 backdrop-blur-sm" />
</Carousel>
```

#### `SwiperCarousel`
터치 스와이프, freeMode, autoplay, pagination 지원. Embla보다 모바일 제스처에 강함.

**핵심 주의**: `modules` 배열은 컴포넌트 외부 상수로 고정됨 (내부 구현 참고). 매 렌더마다 새 배열을 만들면 Swiper 재초기화 버그 발생.

```tsx
<SwiperCarousel
  slidesPerView={2.3}
  spaceBetween={12}
  freeMode
  loop
  showPagination
  autoplay={{ delay: 3000, disableOnInteraction: false }}
  breakpoints={{ 640: { slidesPerView: 3.3 } }}
>
  {items.map(item => <Card key={item.id}>...</Card>)}
</SwiperCarousel>
```

**CSS 의존성**: `globals.css`에 Swiper CSS import 필요. 이미 포함됨:
```css
@import "swiper/css";
@import "swiper/css/pagination";
@import "swiper/css/free-mode";
```

---

### 캘린더

#### `MonthCalendar` · `CalendarEvent` (type)
서버 컴포넌트에서 사용 가능 (순수 데이터 계산).

```tsx
<MonthCalendar
  year={2024}
  month={5}
  today={7}
  events={[
    { day: 7, label: "엄마: 치과 예약", color: "mauve" },
    { day: 12, label: "가족 바베큐", color: "amber" },
  ]}
/>
```

`color` 옵션: `"primary" | "blue" | "green" | "mauve" | "amber"`

---

## 페이지 컴포넌트

### `DashboardPage` (`/`)
**파일**: `src/app/page.tsx` · Server Component

하드코딩 정적 데이터 (추후 API 교체 예정): `scheduleItems`, `shoppingItems`, `weatherDays`, `familyPhotos`

### `ComponentsPage` (`/components`)
**파일**: `src/app/components/page.tsx` · Server Component  
**보조 파일**: `src/app/components/carousel-demo.tsx` · Client Component (`"use client"`)

공통 컴포넌트 데모 페이지. 신규 컴포넌트 추가 시 이 페이지에도 데모 섹션을 추가한다.

캐러셀 관련 코드는 `carousel-demo.tsx`로 분리되어 있음 — Embla/Swiper 모두 `useState`/`useEffect`를 사용하므로 서버 컴포넌트인 `page.tsx`에서 직접 사용 불가.

---

## 상태관리 — `src/shared/store/`

```ts
import { createStore, createPersistedStore } from "@/shared/store"

const useMyStore = createStore<MyState>("my-store", (set) => ({
  value: "",
  setValue: (v: string) => set({ value: v }),
}))
```

| 함수 | devtools | persist | 용도 |
|------|----------|---------|------|
| `createStore(name, initializer)` | O | X | 일반 UI 상태 |
| `createPersistedStore(name, initializer)` | O | O (localStorage) | 새로고침 유지 |

---

## 폼 — `src/shared/form/`

```ts
import { useForm, FormProvider, Controller, type SubmitHandler } from "@/shared/form"
```

react-hook-form의 주요 hook/컴포넌트/타입을 re-export.

---

## 유틸리티

### `cn()` — `src/shared/utils/`
```ts
import { cn } from "@/shared/utils"
cn("text-sm", isActive && "font-bold", className)
```

### Config — `src/shared/config/`
```ts
import { APP_NAME, SIDEBAR_NAV, BOTTOM_NAV, MEMBERS, EXTRA_MEMBER_COUNT } from "@/shared/config"
```
