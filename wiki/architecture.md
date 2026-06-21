# 아키텍처

## 폴더 구조

```
kinfolk-work/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── layout.tsx          # Root layout (font, sidebar, bottom-nav 마운트)
│   │   ├── page.tsx            # 대시보드 홈 (/)
│   │   └── globals.css         # Tailwind v4 + 디자인 시스템 CSS 변수
│   │
│   └── shared/                 # 전역 공유 모듈 (모든 공통 코드)
│       ├── ui/                 # UI 컴포넌트 (shadcn 설치 경로)
│       │   ├── button.tsx      # shadcn Button
│       │   ├── badge.tsx       # shadcn Badge
│       │   ├── sidebar.tsx     # 데스크탑 좌측 사이드바 (lg+ 표시)
│       │   ├── top-bar.tsx     # 상단 헤더 (반응형)
│       │   └── bottom-nav.tsx  # 모바일 하단 네비게이션 (lg 미만 표시)
│       ├── config/             # 앱 전반의 정적 설정값
│       │   ├── app.ts          # APP_NAME, APP_DESCRIPTION, APP_LANG
│       │   ├── nav.ts          # SIDEBAR_NAV, BOTTOM_NAV 배열
│       │   ├── members.ts      # MEMBERS, EXTRA_MEMBER_COUNT, Member 타입
│       │   └── index.ts        # barrel export
│       ├── utils/
│       │   └── index.ts        # cn() 유틸 함수
│       ├── form/
│       │   └── index.ts        # react-hook-form 공통 re-export
│       ├── store/
│       │   └── index.ts        # zustand createStore / createPersistedStore 헬퍼
│       ├── lib/                # shadcn lib 파일 설치 경로
│       └── hooks/              # shadcn hooks 설치 경로
│
├── public/                     # 정적 에셋
├── components.json             # shadcn 설정 (style: "base-nova")
├── next.config.ts
├── tailwind.config (v4, inline via CSS)
└── tsconfig.json
```

---

## 핵심 설계 결정

### 1. `shared/` 폴더 분리

**이유**: 컴포넌트, 상태관리, 유틸리티 등 모든 공통 코드를 `src/shared/` 한 곳에서 관리.

**규칙**:
- `shared/ui/` — shadcn 컴포넌트 + 레이아웃 컴포넌트 (shadcn CLI 설치 경로)
- `shared/utils/` — 순수 함수 유틸리티 (`cn` 등)
- `shared/config/` — 앱 전반의 정적 설정값
- `shared/form/` — react-hook-form 공통 re-export
- `shared/store/` — zustand store 헬퍼 (`createStore`, `createPersistedStore`)
- `shared/lib/` — shadcn lib 파일 설치 경로
- `shared/hooks/` — shadcn hooks 설치 경로

### 2. 반응형 레이아웃 전략

| 브레이크포인트 | 레이아웃 |
|--------------|---------|
| `< lg` (모바일) | 사이드바 없음, 상단 로고+아이콘, 하단 네비게이션 바, 1열 그리드 |
| `≥ lg` (데스크탑) | 좌측 사이드바 (`w-56`), 상단 날짜+날씨+검색, 2열 그리드 |

**구현 방식**: Tailwind `lg:` prefix로 반응형 처리. 별도 페이지/라우트 분기 없음.

**모바일 전용 요소**:
- `BottomNav` 컴포넌트 (`lg:hidden fixed bottom-0`)
- 페이지 콘텐츠 `pb-16` (하단 nav 가림 방지)
- 인라인 FAB 버튼 (`lg:hidden fixed bottom-20`)
- 날씨·가족 아바타 블록 (`lg:hidden`)

**데스크탑 전용 요소**:
- `Sidebar` (`hidden lg:flex`)
- 빠른 추가 카드 (`hidden lg:block`)
- 일정 태그 라벨 (`hidden lg:inline-flex`)

### 3. CSS 변수 기반 테마

Tailwind v4는 `@theme inline { }` 블록으로 CSS 변수를 Tailwind 유틸리티에 매핑.
디자인 토큰 변경 시 `globals.css`의 `:root` 블록만 수정하면 전체 적용.

### 4. Root Layout 구조

```tsx
<html>
  <body>
    <Sidebar />           {/* hidden lg:flex — 데스크탑만 */}
    <div>                 {/* flex-1, pb-16 lg:pb-0 */}
      {children}          {/* TopBar + 페이지 콘텐츠 */}
    </div>
    <BottomNav />         {/* lg:hidden fixed — 모바일만 */}
  </body>
</html>
```

---

## Import 경로 규칙

```ts
// 유틸리티
import { cn } from "@/shared/utils";

// 설정값
import { SIDEBAR_NAV, MEMBERS, APP_NAME } from "@/shared/config";

// shadcn 컴포넌트
import { Button } from "@/shared/ui/button";
import { Badge } from "@/shared/ui/badge";

// 레이아웃 컴포넌트
import { Sidebar } from "@/shared/ui/sidebar";
import { TopBar } from "@/shared/ui/top-bar";
import { BottomNav } from "@/shared/ui/bottom-nav";

// 상태관리
import { createStore, createPersistedStore } from "@/shared/store";

// 폼
import { useForm, FormProvider } from "@/shared/form";
```

---

## 주요 외부 의존성

| 패키지 | 용도 |
|--------|------|
| `next/font/google` | Plus Jakarta Sans 로드 |
| `next/navigation` | `usePathname` (활성 링크 판별) |
| `lucide-react` | 모든 아이콘 |
| `shadcn` | UI 컴포넌트 CLI 및 베이스 |
| `@base-ui/react` | shadcn base-nova 스타일의 기반 |
| `react-hook-form` | 폼 상태 관리 (`@/shared/form`에서 re-export) |
| `zustand` | 전역 상태 관리 (`@/shared/store`에서 헬퍼 제공) |
