# Kinfolk 프로젝트 태스크 현황

> 최종 업데이트: 2026-06-24

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
| A-7 | 온보딩 workspace/profile 화면 연동 | frontend | ✅ 완료 | createWorkspace + updateMe 연동 |
| A-8 | `/workspaces` 목록·선택 화면 연동 | frontend | ✅ 완료 | getWorkspaces + selectWorkspace 연동 |
| A-9 | 사이드바 워크스페이스 전환 연동 | frontend | ✅ 완료 | store 기반 표시 + /workspaces 링크 |

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
| B-10 | 캘린더 API 계약 정합성 수정 | backend+frontend | ✅ 완료 | Jackson ISO 설정 + createCalendarEvent 반환 타입 void |

---

## C. 장보기 (Shopping)

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| C-1 | 장보기 페이지 퍼블리싱 | publisher | ✅ 완료 | `src/app/shopping/page.tsx`, Checkbox 신규 |
| C-2 | 항목 추가 모달 퍼블리싱 | publisher | ✅ 완료 | Dialog 재사용, AddItemDialog |
| C-3 | DDL + 장보기 도메인 API 구현 (#9) | backend | ✅ 완료 | shopping DDL, 전체 CRUD |
| C-4 | 장보기 API 정의 문서 → frontend 전달 (#11) | backend | ✅ 완료 | `shared/api/shopping.ts` |
| C-5 | 장보기 API 클라이언트 타입/함수 구현 (#12) | frontend | ✅ 완료 | `shared/api/shopping.ts` |
| C-6 | 장보기 페이지 API 연동 (mock → 실제) (#13) | frontend | ✅ 완료 | optimistic update + lazy-seed |

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

## G. 플래너 (Planner)

> `/planner` 3개 도메인 mock → 실제 API 전환. DDL/계약은 frontend mock(`shared/api/{schedule-polls,place-suggestions,settlement-expenses}.ts`) 기준 확정. 전 엔드포인트 인증+멤버십 검증, vote=POST 토글, GET=workspaceId 쿼리파람.

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| G-1 | DDL 3종 (SCHEDULE_POLL/CANDIDATE/VOTE, PLACE_SUGGESTION/VOTE, SETTLEMENT_EXPENSE) | backend | ✅ 완료 | 테이블 생성 완료 |
| G-2 | 희망일정 API 4종 (목록/요약/생성/투표 토글, **익명**) | backend | ✅ 완료 | USER_ID 미노출 검증 완료, votedByMe 세션 기준 |
| G-3 | 장소제안 API 3종 (목록/생성/투표 토글) | backend | ✅ 완료 | UNIQUE(PLACE_ID,USER_ID) 중복방지 |
| G-4 | 장소 preview API (OG 메타 추출) | backend | ✅ 완료 | Jsoup + SSRF 가드 |
| G-5 | 정산내역 API 2종 (목록/생성) | backend | ✅ 완료 | date=서버 오늘, payer=자유 텍스트, amount=BIGINT 원 |
| G-6 | Planner API 정의 문서 작성 → frontend 전달 | backend | ✅ 완료 | PM 코드 검증 완료 (URL/필드 mock과 일치) |
| G-7 | `schedule-polls.ts` mock → 실제(apiFetch) | frontend | ✅ 완료 | page.tsx 무수정, 함수 반환 타입 동일 유지 |
| G-8 | `place-suggestions.ts` mock → 실제 | frontend | ✅ 완료 | resolvePlacePreview 서버 OG 추출 연동 |
| G-9 | `settlement-expenses.ts` mock → 실제 | frontend | ✅ 완료 | date 서버 생성, amount 정수 원 |
| G-10 | planner 페이지 연동 검증 (ApiResponse `.data` 언래핑 정합) | frontend | ✅ 완료 | tsc --noEmit 에러 없음 |
| G-11 | (low) `isAnonymous` 직렬화·매핑 확인 | backend | ✅ 완료 | (a) `@JsonProperty("isAnonymous")` 적용·검증 (b) resultMap 미매핑→DTO 기본 true 유지. PM 코드 검증 완료 |

---

## H. 플래너 CRUD (Planner Entity)

> 플래너 엔티티(제목/참가자/색상/캘린더/itinerary) CRUD. G(투표·장소·정산 하위 도메인)와 별개. 계약은 frontend `shared/api/planner.ts`(이미 실제 클라이언트) 기준 1:1. **DDL=A안 JSON 컬럼 확정**, itinerary=PATCH 전체교체+클라이언트 id 보존.

| # | 작업 | 담당 | 상태 | 비고 |
|---|------|------|------|------|
| H-1 | DDL `PLANNER` (JSON 컬럼: PARTICIPANTS/ITINERARY) | backend | ✅ 완료 | 테이블 생성 완료 |
| H-2 | GET `/planners?workspaceId=` 목록 | backend | ✅ 완료 | PM 검증: 계약 일치 |
| H-3 | GET `/planners/{plannerId}?workspaceId=` 상세 | backend | ✅ 완료 | PM 검증 |
| H-4 | POST `/planners` 생성 | backend | ✅ 완료 | color="blue", calendar=현재 연/월 기본값 검증 |
| H-5 | PATCH `/planners/{plannerId}/itinerary` 전체 교체 | backend | ✅ 완료 | ITINERARY JSON 통째 덮어쓰기, 클라 id 보존 |
| H-6 | JSON ↔ List<DTO> MyBatis TypeHandler | backend | ✅ 완료 | ListStringTypeHandler + PlannerItineraryTypeHandler |
| H-7 | 3개 페이지 mock→실제 전환 + 배럴 정리 | frontend | ✅ 완료 | PM 검증: mock 잔존 없음, tsc 무오류 |

---

## 현재 블로커

1. **D-2 미착수** — 대시보드 API 연동 (A~C 완료됨, D로 이동 가능)
