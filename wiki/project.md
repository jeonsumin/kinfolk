# 프로젝트 개요

## 서비스 정보

- **이름**: Kinfolk Table
- **설명**: 가족 구성원이 일정, 사진, 쇼핑 리스트 등을 함께 관리하는 패밀리 대시보드 앱
- **브랜드 컨셉**: Calm Sophistication — 차분하고 세련된 경험

## 기술 스택

| 항목 | 버전 / 도구 |
|------|------------|
| Framework | Next.js **16.2.9** (App Router) |
| Runtime | React **19.2.4** |
| 언어 | TypeScript 5 |
| 스타일 | Tailwind CSS **v4** (`@import "tailwindcss"` 방식) |
| UI 컴포넌트 | shadcn/ui (`style: "base-nova"`) |
| 기반 UI 라이브러리 | `@base-ui/react ^1.6.0` |
| 아이콘 | `lucide-react ^1.21.0` |
| 유틸리티 | `clsx`, `tailwind-merge`, `class-variance-authority` |
| 애니메이션 | `tw-animate-css` |
| 캐러셀 (1) | `embla-carousel-react ^8.6.0` (shadcn Carousel 기반) |
| 캐러셀 (2) | `swiper ^12.2.0` (터치 스와이프·freeMode·autoplay) |
| 폼 | `react-hook-form ^7.80.0` |
| 상태관리 | `zustand ^5.0.14` |

## ⚠️ Next.js 주의사항 (AGENTS.md)

> "This is NOT the Next.js you know"  
> 버전 16은 Breaking Change가 있을 수 있음. 코드 작성 전 반드시
> `node_modules/next/dist/docs/` 의 관련 가이드를 확인할 것.

- `params`는 **Promise**로 반환됨 → `await params` 또는 `use(params)` 필요
- `layout.tsx` params 타입: `Promise<{ slug: string }>`

## AI 코딩 규칙 (CLAUDE.md)

프로젝트 루트의 `CLAUDE.md`에 AI 코딩 규칙이 정의됨:

- **컴포넌트 우선 원칙**: `@/shared/ui` 배럴 export에서만 import, raw HTML 대체 금지
- **컴포넌트 카탈로그**: 20+ 공통 컴포넌트 전체 API 문서화
- **디자인 토큰 강제**: 색상 하드코딩 금지, CSS 변수만 사용
- **신규 컴포넌트 절차**: `src/shared/ui/` 추가 → `index.ts` export → `/components` 데모 추가

## 실행 방법

```bash
cd /config/workspace/kinfolk-work/workspace
npm run dev    # 개발 서버 (http://localhost:3000)
npm run build  # 프로덕션 빌드
npm run start  # 프로덕션 서버
npm run lint   # ESLint
```

## 현재 라우트

| 경로 | 파일 | 설명 |
|------|------|------|
| `/` | `src/app/page.tsx` | 대시보드 홈 |
| `/components` | `src/app/components/page.tsx` | UI 컴포넌트 라이브러리 데모 |

## 작업 디렉토리

원본 `/config/workspace/kinfolk/`은 root 소유로 직접 편집 불가.  
`cp -r`로 `/config/workspace/kinfolk-work/`에 복사 후 작업 중.

실제 소스 경로: `/config/workspace/kinfolk-work/workspace/src/`
