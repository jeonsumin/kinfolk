# 컴포넌트 목록

## 레이아웃 컴포넌트

---

### `<Sidebar />`

**파일**: `src/shared/ui/sidebar.tsx`
**타입**: Client Component (`"use client"`)
**표시 조건**: `hidden lg:flex` — 데스크탑(lg+)에서만 표시

**구조**:
```
aside.hidden.lg:flex (w-56, bg-sidebar)
├── div — 로고 (Kinfolk / Table)
├── nav — 네비게이션 링크 목록
│   └── Link × SIDEBAR_NAV.length
└── div — 하단 "뽀빠 추가" 버튼
```

**데이터 소스**: `SIDEBAR_NAV` from `@/shared/config`

**활성 링크 판별**: `usePathname() === href`

**활성 스타일**: `bg-sidebar-accent text-sidebar-accent-foreground font-medium`

---

### `<TopBar />`

**파일**: `src/shared/ui/top-bar.tsx`
**타입**: Client Component

**반응형 동작**:

| 영역 | 모바일 | 데스크탑 |
|------|--------|---------|
| 왼쪽 | `APP_NAME` 텍스트 | 현재 날짜 (ko-KR) |
| 중앙-우측 | 검색 버튼 (아이콘만) + 벨 아이콘 + 아바타 | 날씨 + 검색(텍스트+아이콘) + 아바타 |
| 아바타 추가 | `+{EXTRA_MEMBER_COUNT}` 뱃지 표시 | 뱃지 없음 |

**데이터 소스**: `MEMBERS`, `EXTRA_MEMBER_COUNT`, `APP_NAME` from `@/shared/config`

---

### `<BottomNav />`

**파일**: `src/shared/ui/bottom-nav.tsx`
**타입**: Client Component
**표시 조건**: `lg:hidden fixed bottom-0` — 모바일에서만 표시
**z-index**: `z-50`

**구조**:
```
nav (fixed bottom-0, h-16)
├── NavItem — 홈 (/)
├── NavItem — 캘린더 (/schedule)
├── div -top-4 — FAB 추가 버튼 (Link → /add)
├── NavItem — 사진 (/photos)
└── div.w-12 — FAB 균형을 위한 스페이서
```

**데이터 소스**: `BOTTOM_NAV` from `@/shared/config`

**내부 컴포넌트**: `NavItem` (비공개)
- active 시 `strokeWidth={2.2}`, 비활성 시 `strokeWidth={1.8}`

---

## 페이지 컴포넌트

---

### `DashboardPage` (홈 `/`)

**파일**: `src/app/page.tsx`
**타입**: Server Component

**레이아웃 구조**:
```
div (flex-col, h-full)
├── <TopBar />
└── div (flex-1, overflow-y-auto)
    └── div.max-w-6xl (px-4 lg:px-6)
        ├── Greeting — "좋은 아침이에요, 이주님!"
        ├── [Mobile only] 날씨 + 가족 아바타 블록
        └── grid (grid-cols-1 lg:grid-cols-[1fr_280px])
            ├── 왼쪽 열
            │   ├── 오늘의 일정 (scheduleItems)
            │   └── 가족 사진 공유 (familyPhotos)
            └── 오른쪽 열
                ├── [Desktop only] 빠른 추가 카드
                ├── 쇼핑 리스트 (shoppingItems + 진행바)
                └── 다가오는 날씨 (weatherDays)
```

**하드코딩 데이터** (추후 API로 교체 예정):
- `scheduleItems` — 일정 배열 (time, title, subtitle, tag, done, accent)
- `shoppingItems` — 쇼핑 목록 (label, done)
- `weatherDays` — 날씨 3일치 (day, icon, high, low)
- `familyPhotos` — 사진 placeholder (alt, bg)

**`MEMBERS`**: `@/shared/config`에서 import (모바일 아바타 블록)

---

## shadcn UI 컴포넌트

모두 `src/shared/ui/`에 설치됨. `npx shadcn add <name>`으로 추가 시 자동으로 이 경로에 생성됨.

---

### `<Button />`

**파일**: `src/shared/ui/button.tsx`
**import**: `import { Button } from "@/shared/ui/button"`

**variants**: `default` | `outline` | `secondary` | `ghost` | `destructive` | `link`
**sizes**: `default` | `xs` | `sm` | `lg` | `icon` | `icon-xs` | `icon-sm` | `icon-lg`

---

### `<Badge />`

**파일**: `src/shared/ui/badge.tsx`
**import**: `import { Badge } from "@/shared/ui/badge"`

**variants**: `default` | `secondary` | `destructive` | `outline` | `ghost` | `link`

---

## 상태관리 — `src/shared/store/`

**import**: `import { createStore, createPersistedStore } from "@/shared/store"`

| 함수 | devtools | persist | 용도 |
|------|----------|---------|------|
| `createStore(name, initializer)` | O | X | 일반 UI 상태 |
| `createPersistedStore(name, initializer)` | O | O (localStorage) | 새로고침 후 유지 필요한 상태 |

**사용 패턴**:
```ts
const useMyStore = createStore<MyState>("my-store", (set) => ({
  value: "",
  setValue: (v: string) => set({ value: v }),
}));

const { value, setValue } = useMyStore();
// 또는 선택자로 최적화:
const value = useMyStore((s) => s.value);
```

---

## 폼 — `src/shared/form/`

**import**: `import { useForm, FormProvider, Controller } from "@/shared/form"`

react-hook-form의 주요 hook/컴포넌트와 타입을 re-export.

| export | 종류 |
|--------|------|
| `useForm`, `useFormContext`, `useWatch`, `useFieldArray` | hooks |
| `FormProvider`, `Controller` | components |
| `UseFormReturn`, `FieldValues`, `SubmitHandler` 등 | types |

---

## Shared 모듈

---

### `cn()` — `src/shared/utils/index.ts`

```ts
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]): string
```

clsx + tailwind-merge 조합. 조건부 클래스 병합에 사용.

---

### Config — `src/shared/config/`

#### `app.ts`
```ts
APP_NAME: string        // "Kinfolk Table"
APP_DESCRIPTION: string // "가족을 위한 공간"
APP_LANG: string        // "ko"
```

#### `nav.ts`
```ts
SIDEBAR_NAV: readonly NavItem[]  // 8개 항목
BOTTOM_NAV: readonly NavItem[]   // 3개 항목 (홈, 캘린더, 사진)

type NavItem = { href: string; label: string; icon: LucideIcon }
```

#### `members.ts`
```ts
MEMBERS: Member[]              // 3명
EXTRA_MEMBER_COUNT: number     // 2

type Member = {
  name: string;
  initials: string;
  color: string;  // hex
}
```
