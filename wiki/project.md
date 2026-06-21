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
| 기본 UI 라이브러리 | `@base-ui/react ^1.6.0` |
| 아이콘 | `lucide-react ^1.21.0` |
| 유틸리티 | `clsx`, `tailwind-merge`, `class-variance-authority` |
| 애니메이션 | `tw-animate-css` |
| 폼 | `react-hook-form ^7.80.0` |
| 상태관리 | `zustand ^5.0.14` |

## ⚠️ Next.js 주의사항 (AGENTS.md)

> "This is NOT the Next.js you know"
>
> 버전 16은 Breaking Change가 있을 수 있음. 코드 작성 전 반드시
> `node_modules/next/dist/docs/` 의 관련 가이드를 확인할 것.

주요 확인 사항:
- `params`는 **Promise**로 반환됨 → `await params` 또는 `use(params)` 필요
- `layout.tsx` `params` 타입: `Promise<{ slug: string }>`

## 실행 방법

```bash
cd /config/workspace/kinfolk-work
npm run dev    # 개발 서버
npm run build  # 빌드
npm run start  # 프로덕션 서버
```

## 작업 디렉토리 경위

원본 `/config/workspace/kinfolk/`은 root 소유라 직접 편집 불가.
`cp -r`로 `/config/workspace/kinfolk-work/`에 복사 후 작업 중.
