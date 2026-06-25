# 워크스페이스 멤버 초대/관리 API 정의서

> Base URL: `/api/v1.0`  
> 모든 요청은 `Authorization: Bearer <JWT>` 헤더 필수  
> 공통 에러 응답 형식: `{ "message": "에러 메시지" }`

---

## 공통 규칙

### 권한 레벨
| 권한 | 설명 |
|------|------|
| `OWNER` | 멤버 관리·초대·워크스페이스 삭제 가능 |
| `MEMBER` | 데이터 생성·수정 가능, 멤버 관리 불가 |

### inviteUrl 구성
```
{FRONTEND_URL}/invite/{token}
```
- `FRONTEND_URL`: 서버 환경변수 `FRONTEND_URL` (기본값: `http://localhost:3000`)
- `token`: 32자 hex UUID (하이픈 제거)

---

## 1. 초대 생성

### `POST /workspace/{wsId}/invitations`

**권한**: OWNER  
**설명**: 이메일 초대 또는 오픈링크 초대를 생성한다.  
- `email` 있으면 → 이메일 초대 (현재 메일 서버 미설정으로 로그만 기록, 추후 발송)  
- `email` 없으면 → 오픈링크 토큰만 발급

#### Request Body
```json
{
  "email": "user@example.com",   // optional. 없으면 오픈링크
  "authority": "MEMBER"           // optional. 기본값 MEMBER (OWNER | MEMBER)
}
```

#### Response `201 Created`
```json
{
  "invitationId": "WI202506250001",
  "wsId": "W202506250001",
  "wsNm": null,
  "inviteEmail": "user@example.com",
  "inviteToken": "a1b2c3d4e5f6...",
  "authority": "MEMBER",
  "status": "PENDING",
  "expireDt": "2026-06-28T10:00:00.000+00:00",
  "acceptedUserId": null,
  "acceptedDt": null,
  "registDt": null,
  "registId": "userId",
  "inviterName": null,
  "inviteUrl": "http://localhost:3000/invite/a1b2c3d4e5f6..."
}
```

#### Error
| 상태코드 | 설명 |
|---------|------|
| `403 Forbidden` | OWNER 아님 |
| `400 Bad Request` | 이메일 형식 오류 |

---

## 2. 초대 목록 조회

### `GET /workspace/{wsId}/invitations`

**권한**: OWNER  
**설명**: 워크스페이스에서 보낸 초대 목록을 반환한다. 만료된 PENDING 초대는 EXPIRED로 자동 갱신 후 반환.

#### Response `200 OK`
```json
[
  {
    "invitationId": "WI202506250001",
    "wsId": "W202506250001",
    "wsNm": null,
    "inviteEmail": "user@example.com",
    "inviteToken": "a1b2c3...",
    "authority": "MEMBER",
    "status": "PENDING",
    "expireDt": "2026-06-28T10:00:00.000+00:00",
    "acceptedUserId": null,
    "acceptedDt": null,
    "registDt": "2026-06-25T10:00:00.000+00:00",
    "registId": "userId",
    "inviterName": null,
    "inviteUrl": "http://localhost:3000/invite/a1b2c3..."
  }
]
```

#### Error
| 상태코드 | 설명 |
|---------|------|
| `403 Forbidden` | OWNER 아님 |

---

## 3. 초대 취소

### `DELETE /workspace/{wsId}/invitations/{invitationId}`

**권한**: OWNER  
**설명**: 초대 상태를 `REVOKED`로 변경한다. 이미 ACCEPTED된 초대도 취소 가능하나 기존 멤버십에는 영향 없음.

#### Response `204 No Content`

#### Error
| 상태코드 | 설명 |
|---------|------|
| `403 Forbidden` | OWNER 아님 |
| `404 Not Found` | 해당 wsId에 초대 없음 |

---

## 4. 초대 토큰 조회 (수락 페이지용)

### `GET /invitations/{token}`

**권한**: 인증된 사용자  
**설명**: 토큰 유효성 및 워크스페이스·초대자 정보 반환. 만료된 PENDING은 EXPIRED로 lazy 갱신.

#### Response `200 OK`
```json
{
  "invitationId": "WI202506250001",
  "wsId": "W202506250001",
  "wsNm": "우리 가족 워크스페이스",
  "inviteEmail": "user@example.com",
  "inviteToken": "a1b2c3...",
  "authority": "MEMBER",
  "status": "PENDING",
  "expireDt": "2026-06-28T10:00:00.000+00:00",
  "acceptedUserId": null,
  "acceptedDt": null,
  "registDt": "2026-06-25T10:00:00.000+00:00",
  "registId": "inviterId",
  "inviterName": "홍길동",
  "inviteUrl": "http://localhost:3000/invite/a1b2c3..."
}
```

> **프론트 활용 포인트**: `status` 필드로 만료/취소/이미수락 여부 표시.  
> `status`: `PENDING` | `ACCEPTED` | `EXPIRED` | `REVOKED`

#### Error
| 상태코드 | 설명 |
|---------|------|
| `404 Not Found` | 존재하지 않는 토큰 |

---

## 5. 초대 수락

### `POST /invitations/{token}/accept`

**권한**: 인증된 사용자  
**설명**: 현재 로그인 사용자를 워크스페이스 멤버로 등록한다.

**이메일 초대인 경우 수락자 이메일 정책**: 초대된 이메일과 수락자 이메일이 달라도 허용 (오픈링크와 동일). 프론트에서 이메일 확인 안내를 권장.

**멱등성**: 이미 멤버인 경우 `204 No Content` 반환 (중복 등록하지 않음).

#### Response `204 No Content`

#### Error
| 상태코드 | 설명 |
|---------|------|
| `404 Not Found` | 존재하지 않는 토큰 |
| `409 Conflict` | 이미 수락된 초대 |
| `410 Gone` | 만료(`EXPIRED`) 또는 취소(`REVOKED`)된 초대 |

---

## 6. 멤버 내보내기

### `DELETE /workspace/{wsId}/members/{memberId}`

**권한**: OWNER  
**설명**: 워크스페이스에서 지정 멤버를 제거한다.

**가드**:
- 존재하지 않는 멤버 → 404
- 마지막 남은 OWNER 삭제 시도 → 409 (워크스페이스에 OWNER 최소 1명 보장)

#### Response `204 No Content`

#### Error
| 상태코드 | 설명 |
|---------|------|
| `403 Forbidden` | OWNER 아님 |
| `404 Not Found` | 해당 멤버 없음 |
| `409 Conflict` | 마지막 OWNER 삭제 불가 |

---

## 7. 멤버 권한 변경

### `PATCH /workspace/{wsId}/members/{memberId}/authority`

**권한**: OWNER  
**설명**: 지정 멤버의 권한을 변경한다.

**가드**:
- 마지막 OWNER를 MEMBER로 강등 시도 → 409
- 자기 자신도 동일 가드 적용

#### Request Body
```json
{
  "authority": "OWNER"   // OWNER | MEMBER
}
```

#### Response `204 No Content`

#### Error
| 상태코드 | 설명 |
|---------|------|
| `403 Forbidden` | OWNER 아님 |
| `404 Not Found` | 해당 멤버 없음 |
| `409 Conflict` | 마지막 OWNER 강등 불가 |
| `400 Bad Request` | authority 누락 |

---

## 미가입자 가입 후 자동 합류 플로우

```
1. 미가입자가 초대 링크(/invite/{token}) 접속
2. 프론트: GET /invitations/{token} 호출 → 워크스페이스 정보 표시
3. 프론트: 비로그인이면 회원가입/로그인 페이지로 리다이렉트 (token 쿼리 파라미터 유지)
4. 가입/로그인 완료 후 프론트: POST /invitations/{token}/accept 호출
5. 백엔드: 현재 사용자를 WORKSPACE_USER에 등록 → 연동 완료
```

백엔드는 accept API가 **인증된 사용자 기준**으로 동작하므로 회원가입 API 변경 불필요.

---

## DDL 변경 내역

`core_schema_mysql.sql` 상단에 추가:
- `WORKSPACE` — 워크스페이스 테이블 (기존 mapper에 존재했으나 schema 누락)
- `WORKSPACE_USER` — 워크스페이스 멤버 테이블 (동일)
- `WORKSPACE_INVITATION` — 워크스페이스 초대 테이블 (신규)

---

## 환경변수

| Key | 기본값 | 설명 |
|-----|--------|------|
| `FRONTEND_URL` | `http://localhost:3000` | 초대 링크에 사용할 프론트엔드 기본 URL |
