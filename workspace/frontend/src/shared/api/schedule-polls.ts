/**
 * 희망 일정 API Mock.
 *
 * 실제 API로 전환할 때 이 파일의 함수만 동일한 DTO로 교체한다.
 * 익명 투표자는 서버 세션에서만 식별하며, 요청/응답에는 사용자 ID를 포함하지 않는다.
 */
export interface SchedulePollCandidateDTO {
  id: string
  startDt: string // YYYY-MM-DD
  endDt: string // YYYY-MM-DD
  voteCount: number
  votedByMe: boolean
}

export interface SchedulePollDTO {
  id: string
  workspaceId: string
  title: string
  isAnonymous: true
  candidates: SchedulePollCandidateDTO[]
  createdAt: string
}

export interface CreateSchedulePollRequest {
  workspaceId: string
  title: string
  candidates: SchedulePollCandidateRequest[]
}

export interface SchedulePollCandidateRequest {
  startDt: string
  endDt: string
}

export interface ToggleSchedulePollVoteRequest {
  pollId: string
  candidateId: string
}

export interface SchedulePollVoteSummaryDTO {
  startDt: string
  endDt: string
  voteCount: number
}

const pollsByWorkspace = new Map<string, SchedulePollDTO[]>()

const formatDate = (date: Date) => date.toISOString().slice(0, 10)

function copyPoll(poll: SchedulePollDTO): SchedulePollDTO {
  return {...poll, candidates: poll.candidates.map((candidate) => ({...candidate}))}
}

function getPolls(workspaceId: string) {
  const existing = pollsByWorkspace.get(workspaceId)
  if (existing) return existing

  const firstDate = new Date()
  firstDate.setDate(firstDate.getDate() + 7)
  const secondDate = new Date(firstDate)
  secondDate.setDate(secondDate.getDate() + 1)
  const firstEndDate = new Date(firstDate)
  firstEndDate.setDate(firstEndDate.getDate() + 1)
  const sample: SchedulePollDTO[] = [{
    id: "poll-sample",
    workspaceId,
    title: "주말 가족 나들이",
    isAnonymous: true,
    candidates: [
      {id: "candidate-sample-1", startDt: formatDate(firstDate), endDt: formatDate(firstEndDate), voteCount: 3, votedByMe: false},
      {id: "candidate-sample-2", startDt: formatDate(secondDate), endDt: formatDate(secondDate), voteCount: 2, votedByMe: false},
    ],
    createdAt: new Date().toISOString(),
  }]
  pollsByWorkspace.set(workspaceId, sample)
  return sample
}

/** GET /api/v1.0/schedule-polls?workspaceId={workspaceId} */
export async function getSchedulePolls(workspaceId: string): Promise<SchedulePollDTO[]> {
  return getPolls(workspaceId).map(copyPoll)
}

/** GET /api/v1.0/schedule-polls/vote-summary?workspaceId={workspaceId} */
export async function getSchedulePollVoteSummary(workspaceId: string): Promise<SchedulePollVoteSummaryDTO[]> {
  const summaries = new Map<string, SchedulePollVoteSummaryDTO>()

  for (const poll of getPolls(workspaceId)) {
    for (const candidate of poll.candidates) {
      const key = `${candidate.startDt}:${candidate.endDt}`
      const summary = summaries.get(key) ?? {startDt: candidate.startDt, endDt: candidate.endDt, voteCount: 0}
      summary.voteCount += candidate.voteCount
      summaries.set(key, summary)
    }
  }

  return [...summaries.values()].sort((a, b) => b.voteCount - a.voteCount)
}

/** POST /api/v1.0/schedule-polls */
export async function createSchedulePoll(request: CreateSchedulePollRequest): Promise<SchedulePollDTO> {
  const poll: SchedulePollDTO = {
    id: `poll-${Date.now()}`,
    workspaceId: request.workspaceId,
    title: request.title.trim(),
    isAnonymous: true,
    candidates: request.candidates.map((candidate, index) => ({
      id: `candidate-${Date.now()}-${index}`,
      startDt: candidate.startDt,
      endDt: candidate.endDt,
      voteCount: 0,
      votedByMe: false,
    })),
    createdAt: new Date().toISOString(),
  }
  getPolls(request.workspaceId).unshift(poll)
  return copyPoll(poll)
}

/** POST /api/v1.0/schedule-polls/{pollId}/votes (익명 세션으로 투표자 판별) */
export async function toggleSchedulePollVote(
  request: ToggleSchedulePollVoteRequest
): Promise<SchedulePollDTO> {
  for (const polls of pollsByWorkspace.values()) {
    const poll = polls.find((item) => item.id === request.pollId)
    const candidate = poll?.candidates.find((item) => item.id === request.candidateId)
    if (!poll || !candidate) continue

    candidate.votedByMe = !candidate.votedByMe
    candidate.voteCount += candidate.votedByMe ? 1 : -1
    return copyPoll(poll)
  }
  throw new Error("희망 일정을 찾을 수 없습니다.")
}
