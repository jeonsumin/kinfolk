# Kinfolk Table — LLM Wiki

이 wiki는 Kinfolk Table 프로젝트의 모든 맥락을 LLM이 빠르게 파악할 수 있도록 구성한 지식 베이스입니다.

## 문서 목록

| 문서 | 설명 |
|------|------|
| [project.md](./project.md) | 프로젝트 개요, 기술 스택, 실행 방법, AI 규칙 요약 |
| [design-system.md](./design-system.md) | 디자인 토큰, 컬러, 타이포그래피, 컴포넌트별 스타일 규칙 |
| [architecture.md](./architecture.md) | 폴더 구조, Server/Client 경계, import 규칙, CSS 아키텍처 |
| [components.md](./components.md) | 전체 컴포넌트 목록, props API, 사용 패턴 |

## 빠른 참조

- **앱 이름**: Kinfolk Table
- **작업 디렉토리**: `/config/workspace/kinfolk-work/workspace/`
- **소스 루트**: `src/`
- **프레임워크**: Next.js 16.2.9 (App Router), React 19, Tailwind CSS v4
- **UI 기반**: shadcn/ui (`base-nova` style) + `@base-ui/react ^1.6.0`
- **폰트**: Plus Jakarta Sans
- **디자인 문서**: `/config/workspace/kinfolk-work/DESIGN.md`
- **AI 규칙**: `/config/workspace/kinfolk-work/workspace/CLAUDE.md`

## 핵심 규칙 요약

```ts
// 공통 UI — 항상 배럴 export 사용
import { Button, Card, Label, Switch, Table } from "@/shared/ui"

// 레이아웃 전용 컴포넌트만 예외
import { Sidebar } from "@/shared/ui/sidebar"
```

새 컴포넌트 추가 시: `src/shared/ui/` → `index.ts` export → `/components` 데모 → `CLAUDE.md` 문서화

## 현재 컴포넌트 목록 (요약)

| 카테고리 | 컴포넌트 |
|---------|---------|
| 기본 액션 | `Button`, `Switch` |
| 텍스트 | `Label` (variant/size), `Badge`, `Chip` |
| 카드 | `Card`, `ImageCard`, `ImageOverlayCard` |
| 폼 | `Input`, `Textarea` |
| 피드백 | `Progress`, `Skeleton`, `EmptyState` |
| 구조 | `SectionHeader`, `Separator`, `ListItem`, `ListGroup` |
| 미디어 | `Avatar`, `AvatarGroup`, `AvatarBadge` |
| 데이터 | `Table` (+ TableHeader/Body/Row/Head/Cell) |
| 캐러셀 | `Carousel` (Embla), `SwiperCarousel` |
| 캘린더 | `MonthCalendar` |
| 레이아웃 | `Sidebar`, `TopBar`, `BottomNav` (배럴 미포함) |
