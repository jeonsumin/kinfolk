@AGENTS.md

# UI 컴포넌트 규칙

## 핵심 원칙

1. **공통 컴포넌트 우선** — UI를 작성할 때 `@/shared/ui`에 있는 컴포넌트를 반드시 먼저 확인하고 사용한다. 동일한 역할을 하는 raw HTML(`<button>`, `<div>`, `<input>` 등)을 직접 작성하지 않는다.
2. **단일 import 경로** — 항상 배럴 export에서 import한다. 개별 파일(`@/shared/ui/button`)에서 직접 import하지 않는다.
3. **새 컴포넌트 추가 절차** — 기존 컴포넌트로 해결이 안 될 때만 신규 컴포넌트를 제안한다. 신규 컴포넌트는 반드시 `src/shared/ui/`에 추가하고 `index.ts`에 export한다. 페이지 내 인라인 정의 금지.
4. **디자인 토큰 사용** — 하드코딩된 색상(`#475569`, `rgb(...)`) 대신 CSS 변수(`text-primary`, `bg-muted` 등)를 사용한다.

```ts
// ✅ 올바른 import
import { Button, Card, Label } from "@/shared/ui"

// ❌ 금지
import { Button } from "@/shared/ui/button"
import { Button } from "../../shared/ui/button"
```

---

## 컴포넌트 카탈로그

### 기본 액션

#### `Button`
인터랙티브 액션의 기본 단위. 모든 클릭 가능한 요소는 Button을 사용한다.

```tsx
<Button variant="default" | "outline" | "secondary" | "ghost" | "destructive" | "link"
        size="xs" | "sm" | "default" | "lg" | "icon" | "icon-sm" | "icon-xs" | "icon-lg">
```

`buttonVariants`도 export되어 있어 외부에서 className 조합에 사용 가능하다.

#### `Switch`
토글 ON/OFF. 멤버 가시성, 알림 설정 등에 사용한다.

```tsx
<Switch size="sm" | "default"
        defaultChecked={boolean}
        disabled={boolean} />
```

---

### 텍스트 & 레이블

#### `Label`
폼 필드, 섹션 레이블 등 모든 레이블 텍스트에 사용한다. `<p>`, `<span>`으로 대체하지 않는다.

```tsx
<Label variant="default" | "muted" | "destructive" | "required"
       size="sm" | "default" | "lg"
       htmlFor="input-id">
  이름
</Label>
```

- `required` — 자동으로 `*` 표시 추가
- `labelVariants`도 export되어 className 조합에 사용 가능

#### `Badge`
상태 표시, 카운트, 태그 등 작은 강조 요소.

```tsx
<Badge variant="default" | "secondary" | "destructive" | "outline">완료</Badge>
```

`badgeVariants`도 export됨.

#### `Chip`
카테고리, 태그, 필터 등 파스텔 색상의 인라인 레이블. Badge보다 부드러운 느낌.

```tsx
<Chip color="default" | "slate" | "blue" | "green" | "mauve" | "amber"
      size="sm" | "md" | "lg"
      icon={<Icon />}>
  여행
</Chip>
```

`chipVariants`도 export됨.

---

### 카드

#### `Card` (+ `CardHeader`, `CardTitle`, `CardDescription`, `CardContent`, `CardFooter`, `CardAction`)
콘텐츠 컨테이너의 기본 단위. `size="sm"` prop으로 compact 버전 사용 가능.

```tsx
<Card size="default" | "sm">
  <CardHeader>
    <CardTitle>제목</CardTitle>
    <CardDescription>설명</CardDescription>
    <CardAction>액션 버튼</CardAction>
  </CardHeader>
  <CardContent>내용</CardContent>
  <CardFooter>하단 영역</CardFooter>
</Card>
```

#### `ImageCard`
이미지만 표시하는 카드. `src` 없이 `children`으로 배경 지정 가능.

```tsx
<ImageCard src="/photo.jpg" alt="사진" aspectRatio="aspect-[4/3]" />

{/* src 없이 children으로 플레이스홀더 */}
<ImageCard aspectRatio="aspect-video">
  <div className="h-full w-full bg-gradient-to-br from-[#b9c7df] to-[#475569]" />
</ImageCard>
```

#### `ImageOverlayCard`
전체 이미지 위에 왼쪽 하단에 title/subtitle 오버레이. `src` 없을 때 `children`이 배경이 된다.

```tsx
<ImageOverlayCard title="제주도 여행" subtitle="2025.07.12 · 가족 5명"
                  src="/photo.jpg" aspectRatio="aspect-[4/3]" />
```

---

### 폼 요소

#### `Input`
텍스트 입력. `aria-invalid` prop으로 에러 상태 표시.

```tsx
<Input placeholder="홍길동" aria-invalid defaultValue="잘못된 값" />
```

#### `Textarea`
여러 줄 텍스트 입력.

```tsx
<Textarea placeholder="메시지를 입력하세요..." rows={5} />
```

---

### 피드백 & 상태

#### `Progress` (+ `ProgressLabel`, `ProgressValue`, `ProgressTrack`, `ProgressIndicator`)
진행률 표시. 기본 사용은 `<Progress value={80} />`만으로 충분하다.

```tsx
{/* 기본 */}
<Progress value={75} />

{/* 레이블 포함 */}
<Progress value={75}>
  <ProgressLabel>정원 청소</ProgressLabel>
  <ProgressValue />  {/* "75%" 자동 렌더 */}
</Progress>
```

#### `Skeleton`
로딩 상태 플레이스홀더. 실제 콘텐츠와 동일한 크기/형태로 지정한다.

```tsx
<Skeleton className="h-4 w-3/4" />
<Skeleton className="size-8 rounded-full" />
```

#### `EmptyState`
데이터 없음 상태 표시. 빈 목록, 검색 결과 없음 등에 사용한다.

```tsx
<EmptyState icon={<InboxIcon />}
            title="일정이 없습니다"
            description="새로운 일정을 추가해 보세요."
            action={<Button size="sm">추가</Button>} />
```

---

### 레이아웃 & 구조

#### `SectionHeader`
섹션 제목 + 부제목 + 우측 액션 버튼 조합. 반복되는 섹션 헤더 패턴에 사용한다.

```tsx
<SectionHeader title="오늘의 일정"
               subtitle="3개 일정 예정"
               action={<Button size="sm">추가</Button>} />
```

#### `Separator`
구분선. horizontal(기본) / vertical.

```tsx
<Separator />
<Separator orientation="vertical" />
```

#### `ListItem` + `ListGroup`
아이콘 + 제목 + 부제목 + 우측 액션의 리스트 아이템 패턴. `onClick` 있으면 hover 스타일 자동 적용.

```tsx
<ListGroup>
  <ListItem leading={<Icon />} title="멤버" subtitle="5명"
            trailing={<Badge>5</Badge>} onClick={() => {}} />
</ListGroup>
```

---

### 미디어 & 사용자

#### `Avatar` (+ `AvatarImage`, `AvatarFallback`, `AvatarBadge`)
사용자 프로필 이미지. `size="sm" | "default" | "lg"`.

```tsx
<Avatar size="sm">
  <AvatarImage src="/user.jpg" alt="이주" />
  <AvatarFallback>이주</AvatarFallback>
</Avatar>
```

#### `AvatarGroup` + `AvatarGroupCount`
여러 아바타를 겹쳐 표시.

```tsx
<AvatarGroup>
  <Avatar><AvatarFallback>이주</AvatarFallback></Avatar>
  <Avatar><AvatarFallback>김민</AvatarFallback></Avatar>
  <AvatarGroupCount>+3</AvatarGroupCount>
</AvatarGroup>
```

---

### 데이터 & 표

#### `Table` (+ `TableHeader`, `TableBody`, `TableRow`, `TableHead`, `TableCell`, `TableFooter`, `TableCaption`)
정산 내역, 일정 목록 등 표 형태 데이터.

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
      <TableCell className="text-right">₩600,000</TableCell>
    </TableRow>
  </TableBody>
</Table>
```

---

### 캐러셀

#### `Carousel` (Embla / shadcn)
기본 슬라이드 캐러셀. loop, align 옵션 지원.

```tsx
<Carousel opts={{ loop: true }}>
  <CarouselContent>
    <CarouselItem>슬라이드</CarouselItem>
  </CarouselContent>
  <CarouselPrevious className="left-2" />
  <CarouselNext className="right-2" />
</Carousel>
```

#### `SwiperCarousel`
터치 스와이프, freeMode, autoplay, pagination 지원.

```tsx
<SwiperCarousel slidesPerView={2.3} spaceBetween={12}
                freeMode loop showPagination
                autoplay={{ delay: 3000, disableOnInteraction: false }}>
  {items.map(item => <Card key={item.id}>...</Card>)}
</SwiperCarousel>
```

---

### 캘린더

#### `MonthCalendar`
월별 캘린더 그리드. 이벤트 컬러 코딩 지원.

```tsx
<MonthCalendar year={2024} month={5} today={7}
  events={[
    { day: 7, label: "가족 외출", color: "blue" | "green" | "mauve" | "amber" | "primary" }
  ]} />
```

---

## 디자인 토큰

Tailwind CSS 변수로 정의되어 있다. **절대 하드코딩하지 않는다.**

| 용도 | 토큰 | 값 |
|---|---|---|
| 주요 강조색 | `primary` | `#475569` |
| 배경 | `background` | `#f7f9fb` |
| 카드 배경 | `card` | `#ffffff` |
| 경계선 | `border` | `#c4c6cd` |
| 음소거 텍스트 | `muted-foreground` | `#44474c` |
| 에러 | `destructive` | `#ba1a1a` |
| 칩 — 파랑 | `#d2e1f7` / `#516072` | — |
| 칩 — 초록 | `#d1f5e4` / `#2e7d5a` | — |
| 칩 — 보라 | `#ead6f0` / `#7c4d8a` | — |
| 칩 — 황금 | `#fdf0d0` / `#8a6800` | — |

폰트: **Plus Jakarta Sans** (`font-sans` 적용됨)

---

## 신규 컴포넌트 추가 기준

기존 컴포넌트 조합으로 해결이 안 되는 경우에만 신규 컴포넌트를 추가한다.

1. `src/shared/ui/{name}.tsx` 파일 생성
2. `src/shared/ui/index.ts` barrel export에 추가
3. `/components` 페이지에 데모 섹션 추가
4. CVA 패턴 (`variant`, `size`) 사용 권장

**금지 사항**
- 페이지 컴포넌트 내부에 UI 컴포넌트 인라인 정의
- `@/shared/ui`에 이미 있는 역할을 중복 구현
- shadcn 이외의 외부 UI 라이브러리 추가 (Swiper는 예외적으로 허용)
