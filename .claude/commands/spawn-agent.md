# Spawn Agent Team

이 커맨드가 호출되면 아래 절차를 즉시 실행한다. 설명하거나 안내하지 말고 바로 실행한다.

## 실행 절차

### 1단계: 팀 생성

`TeamCreate` 도구를 호출한다:
- `team_name`: `kinfolk`
- `description`: `PM + Backend + Frontend 오케스트레이션 팀 — kinfolk 프로젝트`

### 2단계: 팀원 소환

세 에이전트를 `Agent` 도구로 **동시에** 소환한다 (`run_in_background: true`로 병렬 실행):

| 에이전트 | name | subagent_type | team_name |
|---------|------|---------------|-----------|
| PM | `pm` | `pm-agent` | `kinfolk` |
| Backend | `backend` | `backend-agent` | `kinfolk` |
| Frontend | `frontend` | `frontend-agent` | `kinfolk` |

각 에이전트 prompt:
- **pm**: "당신은 kinfolk 팀의 PM입니다. 팀이 구성됐습니다. 사용자의 기능 요청을 기다리세요. 팀 config는 ~/.claude/teams/kinfolk/config.json에서 확인할 수 있습니다."
- **backend**: "당신은 kinfolk 팀의 Backend 개발자입니다. 팀이 구성됐습니다. pm으로부터 작업 지시를 기다리세요. 팀 config는 ~/.claude/teams/kinfolk/config.json에서 확인할 수 있습니다."
- **frontend**: "당신은 kinfolk 팀의 Frontend 개발자입니다. 팀이 구성됐습니다. pm으로부터 작업 지시를 기다리세요. 팀 config는 ~/.claude/teams/kinfolk/config.json에서 확인할 수 있습니다."

### 3단계: 완료 보고

팀 소환이 완료되면 사용자에게 다음 형식으로 보고한다:

```
팀 소환 완료 ✓

| 역할 | 에이전트 | 상태 |
|------|---------|------|
| PM | pm | 대기 중 |
| Backend | backend | 대기 중 |
| Frontend | frontend | 대기 중 |

pm에게 기능 요청을 전달하면 분석 → 설계 → 승인 → 구현 순으로 진행됩니다.
```
