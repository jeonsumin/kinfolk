# 디자인 시스템

> 원본: `/config/workspace/kinfolk-work/DESIGN.md`  
> 구현: `src/app/globals.css` `:root {}` 블록  
> 데모: `/components` 페이지 — Colors, Typography 섹션

## 브랜드 철학

**Calm Sophistication** — 고에너지·화려함에서 벗어나 차분하고 세련된 경험.

- 스타일: Minimalist + Soft Tonal Layering
- 느낌: 여백 중심, 저채도, 건축적이고 조용한 UI
- 폰트: **Plus Jakarta Sans** 단독 사용

---

## 컬러 팔레트

### 핵심 시스템 컬러

| CSS 변수 | 값 | 용도 |
|---------|----|------|
| `--primary` | `#475569` | Muted Slate — 버튼, 강조, 링크 |
| `--primary-foreground` | `#ffffff` | primary 위 텍스트 |
| `--background` | `#f7f9fb` | 앱 전체 배경 |
| `--foreground` | `#191c1e` | 기본 텍스트 (pure black 금지) |
| `--card` | `#ffffff` | 카드 배경 |
| `--card-foreground` | `#191c1e` | 카드 내 텍스트 |
| `--muted` | `#eceef0` | 비활성 배경, 스켈레톤 |
| `--muted-foreground` | `#44474c` | 보조 텍스트 |
| `--border` | `#c4c6cd` | 구분선, 아웃라인 |
| `--input` | `#e0e3e5` | 인풋 배경 |
| `--ring` | `#475569` | 포커스 링 |
| `--secondary` | `#d2e1f7` | 보조 버튼 배경 |
| `--secondary-foreground` | `#475569` | 보조 버튼 텍스트 |
| `--destructive` | `#ba1a1a` | 에러, 삭제 |
| `--accent` | `#e6e8ea` | hover 배경 |

### 사이드바 전용

| 변수 | 값 |
|------|----|
| `--sidebar` | `#303e51` (진한 네이비) |
| `--sidebar-foreground` | `#ffffff` |
| `--sidebar-accent` | `#3a485b` (활성 메뉴 배경) |
| `--sidebar-accent-foreground` | `#ffffff` |
| `--sidebar-primary` | `#b9c7df` |
| `--sidebar-border` | `#3a485b` |

### Chip / Tag 파스텔 컬러 (디자인 확장)

4가지 의미론적 컬러 — `Chip` 컴포넌트 `color` prop과 `MonthCalendar` 이벤트 color와 연동.

| 이름 | 배경 | 텍스트 | 용도 |
|------|------|--------|------|
| blue | `#d2e1f7` | `#516072` | 교육, 일정, 정보성 |
| green | `#d1f5e4` | `#2e7d5a` | 건강, 완료, 긍정 |
| mauve | `#ead6f0` | `#7c4d8a` | 가족, 특별한 이벤트 |
| amber | `#fdf0d0` | `#8a6800` | 알림, 주의, 외출 |

---

## 타이포그래피

폰트: **Plus Jakarta Sans** (Next.js `next/font/google`로 로드, CSS 변수 `--font-sans`에 매핑)

| 스케일 | 크기 | 굵기 | 행간 | 자간 | Tailwind 조합 |
|--------|------|------|------|------|--------------|
| headline-xl | 40px | 700 | 48px | -0.02em | `text-[40px] font-bold leading-[48px] tracking-[-0.02em]` |
| headline-lg | 32px | 600 | 40px | -0.01em | `text-[32px] font-semibold leading-[40px] tracking-[-0.01em]` |
| headline-md | 24px | 600 | 32px | — | `text-2xl font-semibold leading-8` |
| body-lg | 18px | 400 | 28px | — | `text-lg leading-7` |
| body-md | 16px | 400 | 24px | — | `text-base leading-6` |
| body-sm | 14px | 400 | 20px | — | `text-sm leading-5 text-muted-foreground` |
| caption | 12px | 500-600 | 16px | +0.01em | `text-xs font-medium tracking-wide` |

**Label 컴포넌트 size 매핑**:

| `size` prop | 폰트 | 용도 |
|------------|------|------|
| `sm` | 12px / text-xs | 보조 레이블, 주석 |
| `default` | 14px / text-sm | 기본 폼 레이블 |
| `lg` | 16px / text-base | 강조 레이블, 섹션 제목 |

---

## 간격 (8px 기반 리듬)

| 토큰 | 값 | Tailwind |
|------|----|---------|
| xs | 4px | `p-1` |
| sm | 12px | `p-3` |
| base | 8px | `p-2` |
| md | 24px | `p-6` |
| lg | 48px | `p-12` |
| xl | 80px | `p-20` |
| gutter | 24px | 컬럼 간격 |
| margin-mobile | 16px | `px-4` |
| margin-desktop | 64px | `px-16` |

---

## 모서리 (Radius)

`--radius: 0.5rem` 기준으로 Tailwind v4 `@theme inline`에 계산식으로 정의됨.

| 변수 | 값 | 용도 |
|------|----|------|
| `--radius-sm` | `calc(--radius * 0.6)` ≈ 3px | 아주 작은 요소 |
| `--radius-md` | `calc(--radius * 0.8)` ≈ 6px | 버튼, 인풋 |
| `--radius-lg` | `0.5rem` (8px) | 기본 |
| `--radius-xl` | `calc(--radius * 1.4)` ≈ 11px | 카드 내부 요소 |
| `--radius-2xl` | `calc(--radius * 1.8)` ≈ 14px | 카드 |
| `--radius-3xl` | `calc(--radius * 2.2)` ≈ 18px | 모달, 큰 컨테이너 |

컴포넌트는 `rounded-xl` (≈ 0.75rem) 기준으로 통일.

---

## 깊이 표현 (Elevation)

무거운 drop-shadow 금지. **Tonal Layer + 저대비 아웃라인**으로 깊이 표현.

| 레이어 | 배경 | 용도 |
|--------|------|------|
| Base | `#f7f9fb` | 앱 전체 배경 |
| Mid | `#eceef0` | 콘텐츠 그룹 컨테이너 |
| Top | `#ffffff` + 1px border `#c4c6cd` | 카드, 팝업 |

그림자가 필요한 경우: `blur 20-40px, opacity 3-5%, Slate 틴트`.

---

## 컴포넌트별 스타일 규칙

### 버튼
- `default`: `bg-primary text-white` (solid slate)
- `outline`: ghost + 1px border
- `secondary`: `bg-secondary text-secondary-foreground`
- `ghost`: 배경 없음, hover 시 `bg-muted`

### Label (`labelVariants`)
- `default`: `text-foreground font-medium`
- `muted`: `text-muted-foreground font-normal` — 선택 필드, 부가 설명
- `destructive`: `text-destructive font-medium` — 에러 상태
- `required`: `text-foreground + after:content-['*'] after:text-destructive` — 필수 필드

### 카드
- white 배경, `rounded-xl`, 1px border `ring-1 ring-foreground/10`
- `size="sm"`: `--card-spacing: 0.75rem` (기본 1rem)
- 무거운 drop-shadow 금지

### 인풋
- 배경: `#e0e3e5` (`--input`)
- 포커스: slate gray 아웃라인
- 에러: `aria-invalid` 속성으로 적용

### 캘린더 이벤트 뱃지
배경 80% 불투명도, 텍스트 해당 색상 풀 컬러 적용. 칩 컬러 시스템과 동일.

---

## Tailwind v4 주의사항

- `@import "tailwindcss"` 방식 사용 (v3의 `@tailwind base/components/utilities` 아님)
- CSS 변수 → Tailwind 유틸리티 매핑은 `@theme inline {}` 블록에서 처리
- 서드파티 CSS(Swiper 등)는 `@layer base` 이전에 import해야 reset 충돌 방지
- 임의값 사용 시: `text-[40px]`, `from-[#b9c7df]` 등 표준 임의값 문법 사용
