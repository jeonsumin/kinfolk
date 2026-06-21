# 디자인 시스템

> 원본: `/config/workspace/DESIGN.md`
> 목업: `/config/workspace/docs/dashboard.png` (데스크탑), `docs/mo_dashboard.png` (모바일)

## 브랜드 철학

**Calm Sophistication** — 고에너지·화려함에서 벗어나 차분하고 세련된 경험.
- 타깃: 의도성, 미니멀리즘, 정돈된 멘탈 스페이스를 중시하는 사용자
- 스타일: Minimalist + Soft Tonal Layering
- 느낌: 여백 중심, 저채도, 건축적이고 조용한 UI

---

## 컬러 팔레트

CSS 변수는 `src/app/globals.css`의 `:root` 블록에 정의.

### 핵심 색상

| 변수 | 값 | 용도 |
|------|----|------|
| `--primary` | `#475569` | Muted Slate Gray — 버튼, 강조 |
| `--background` | `#f7f9fb` | 앱 배경 |
| `--card` | `#ffffff` | 카드 배경 |
| `--foreground` | `#191c1e` | 기본 텍스트 (pure black 금지) |
| `--muted-foreground` | `#44474c` | 보조 텍스트 |
| `--border` | `#c4c6cd` | 구분선 |
| `--muted` | `#eceef0` | 비활성 배경 |

### 사이드바 전용

| 변수 | 값 |
|------|----|
| `--sidebar` | `#303e51` (진한 네이비) |
| `--sidebar-foreground` | `#ffffff` |
| `--sidebar-accent` | `#3a485b` (활성 메뉴 배경) |
| `--sidebar-primary` | `#b9c7df` (하단 버튼, 강조) |

### 억센트 (칩·태그용)

| 색상 | 용도 |
|------|------|
| Dusty Blue (`#d2e1f7`) | 보조 정보, 조용한 링크 |
| Sage Green | 긍정 상태, 성장 메타포 |
| Mauve | 3차 하이라이트, 소프트 알림 |

칩 사용 규칙: 배경은 억센트 15% 불투명도, 텍스트는 억센트 풀 컬러.

---

## 타이포그래피

**폰트**: Plus Jakarta Sans (단독 사용)
- Next.js: `Plus_Jakarta_Sans` from `next/font/google`
- CSS 변수: `--font-sans`

| 스케일 | 크기 | 굵기 | 행간 | 자간 |
|--------|------|------|------|------|
| headline-xl | 40px | 700 | 48px | -0.02em |
| headline-lg | 32px | 600 | 40px | -0.01em |
| headline-md | 24px | 600 | 32px | — |
| body-lg | 18px | 400 | 28px | — |
| body-md | 16px | 400 | 24px | — |
| label-md | 14px | 500 | 20px | +0.01em |
| label-sm | 12px | 600 | 16px | — |

---

## 간격 (8px 기반 리듬)

| 토큰 | 값 |
|------|----|
| xs | 4px |
| sm | 12px |
| base | 8px |
| md | 24px |
| lg | 48px |
| xl | 80px |
| gutter | 24px |
| margin-mobile | 16px |
| margin-desktop | 64px |

---

## 모서리 (Rounded)

| 토큰 | 값 | 용도 |
|------|----|------|
| sm | 0.25rem | 작은 요소 |
| DEFAULT | 0.5rem (8px) | 버튼, 인풋, 소형 위젯 |
| md | 0.75rem | — |
| lg | 1rem (16px) | 카드, 모달 |
| xl | 1.5rem (24px) | 히어로, 배경 컨테이너 |

CSS 변수: `--radius: 0.5rem`

---

## 깊이 표현 (Elevation)

그림자 대신 **Tonal Layer**와 저대비 아웃라인으로 깊이 표현.

1. **Base Layer**: Off-white 배경 (`#f7f9fb`)
2. **Mid Layer**: Light gray 컨테이너 (`#eceef0`) — 콘텐츠 그룹핑
3. **Top Layer**: White 카드 + 1px 테두리 (`#c4c6cd`)

그림자가 꼭 필요한 경우: blur 20-40px, opacity 3-5%, Slate 틴트.

---

## 컴포넌트 규칙

### 버튼
- Primary: `bg-primary text-white` (solid slate)
- Secondary: ghost + 1px border slate
- Tertiary: `bg-muted text-primary` (no border)

### 인풋
- 1px light gray 테두리 → focus 시 slate gray
- 라벨: 필드 위, medium weight, small size

### 카드
- white bg, 8px 또는 16px radius, 1px border
- 무거운 drop-shadow 금지

### 리스트
- 아이템 간 vertical padding 16px
- 구분선: 매우 얇고 연한 gray, 컨테이너 끝에 닿지 않음
