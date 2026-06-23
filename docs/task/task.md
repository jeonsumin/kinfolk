# Kinfolk 프로젝트 태스크 현황

> 최종 업데이트: 2026-06-23

---

## 범례

| 상태 | 의미 |
|------|------|
| ✅ 완료 | 구현·검증 완료 |
| 🔵 진행 중 | 현재 작업 중 |
| ⏳ 대기 | 선행 작업 완료 후 착수 예정 |
| ⏸️ 보류 | 확인/승인 필요로 일시 중단 |
| 🚫 미착수 | 아직 시작 안 함 |

---

## A. 워크스페이스 + 온보딩 프로필

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| A-1 | DDL: WORKSPACE, WORKSPACE_USER | backend | ✅ 완료 | PK/UNIQUE/INDEX/FK CASCADE 적용 |
| A-2 | GET `/api/v1.0/user/me` | backend | ✅ 완료 | 프로필 조회 |
| A-3 | PATCH `/api/v1.0/user/me` | backend | ✅ 완료 | 요청 `displayName` / 응답 `name` |
| A-4 | POST `/api/v1.0/workspace` | backend | ✅ 완료 | 생성자 OWNER 자동 등록 (트랜잭션) |
| A-5 | GET `/api/v1.0/workspace` | backend | ✅ 완료 | 본인 소속 워크스페이스 목록 |
| A-6 | PATCH `/api/v1.0/workspace/{id}/select` | backend | ✅ 완료 | 멤버십 검증, 비멤버 403 |
| A-7 | 온보딩 workspace/profile 화면 연동 | frontend | ⏸️ 보류 | 항목별 진행 회신 대기 중 |
| A-8 | `/workspaces` 목록·선택 화면 연동 | frontend | ⏸️ 보류 | A-7과 동일 |
| A-9 | 사이드바 워크스페이스 전환 연동 | frontend | ⏸️ 보류 | A-7과 동일 |

---

## B. 캘린더 (Calendar)

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| B-1 | CalendarMapper 중복 제거 + 삭제 메서드 추가 | backend | ✅ 완료 | |
| B-2 | CalendarService 버그 수정 (inviteeId 누락 등) | backend | ✅ 완료 | |
| B-3 | CalendarController 8개 엔드포인트 구현 | backend | ✅ 완료 | |
| B-4 | JwtAuthenticationFilter anonymousUser 버그 수정 | backend | ✅ 완료 | |
| B-5 | WS_ID 누락 버그 수정 (CalendarDTO.wsId 추가) | backend | ✅ 완료 | 생성/수정 silent failure 해소 |
| B-6 | 캘린더 페이지 퍼블리싱 (월/주/일 뷰) | publisher | ✅ 완료 | `src/app/calendar/page.tsx` |
| B-7 | 일정 추가 모달 퍼블리싱 | publisher | ✅ 완료 | Dialog 공통 컴포넌트 재사용 |
| B-8 | 캘린더 API 타입/클라이언트 구현 | frontend | ✅ 완료 | `src/shared/api/calendar.ts` |
| B-9 | 캘린더 페이지 API 연동 (mock → 실제) | frontend | ✅ 완료 | 월/주/일 뷰 + 일정 추가 |
| B-10 | 캘린더 API 계약 변경 (리네임/통합 URL/멤버) | - | ⏸️ 보류 | frontend 코드에 근거 없음, 확인 중 |

---

## C. 장보기 (Shopping)

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| C-1 | 장보기 페이지 퍼블리싱 | publisher | ✅ 완료 | `src/app/shopping/page.tsx`, Checkbox 신규 |
| C-2 | 항목 추가 모달 퍼블리싱 | publisher | ✅ 완료 | Dialog 재사용, AddItemDialog |
| C-3 | DDL + 장보기 도메인 API 구현 (#9) | backend | 🔵 진행 중 | shopping DDL, 전체 CRUD |
| C-4 | 장보기 API 정의 문서 → frontend 전달 (#11) | backend | ⏳ 대기 | #C-3 완료 후 |
| C-5 | 장보기 API 클라이언트 타입/함수 구현 (#12) | frontend | ⏳ 대기 | #C-4 완료 후 |
| C-6 | 장보기 페이지 API 연동 (mock → 실제) (#13) | frontend | ⏳ 대기 | #C-5 완료 후 |

---

## D. 대시보드 (Dashboard)

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| D-1 | 대시보드 페이지 퍼블리싱 | publisher | ✅ 완료 | `src/app/page.tsx` 전면 재구성 |
| D-2 | 대시보드 API 연동 | frontend | 🚫 미착수 | shopping 완료 후 검토 |

---

## E. 공통 컴포넌트 (shared/ui)

| 컴포넌트 | 상태 | 비고 |
|---------|------|------|
| Checkbox | ✅ 추가 | base-ui 기반, 장보기에서 신규 |
| Dialog | ✅ 추가 | base-ui 기반, 모달 공통 |
| 기존 컴포넌트 (Button, Avatar 등 20종+) | ✅ 유지 | |

---

## F. 기타 / 인프라

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| F-1 | 워크스페이스 멤버 목록 API (#10) | backend | ✅ 완료 | GET `/workspace/{workspaceId}/members` |
| F-2 | Google OAuth 연동 | - | ⏸️ 2차 분리 | 1차 범위 제외 |
| F-3 | 회원가입 API | - | ⏸️ 2차 분리 | 1차 범위 제외 |

---

## 현재 블로커

1. **C-3 완료 대기** — backend가 shopping API 구현 중, 완료 시 C-4~C-6 순차 착수
2. **A-7~A-9 보류** — frontend 연동 진행 상황 회신 필요
3. **B-10 보류** — 캘린더 API 계약 변경 요구 출처 확인 필요
