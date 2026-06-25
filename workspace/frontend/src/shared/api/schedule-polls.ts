import { apiFetch } from "./client";

export interface SchedulePollCandidateDTO {
  id: string;
  startDt: string;
  endDt: string;
  voteCount: number;
  votedByMe: boolean;
}

export interface SchedulePollDTO {
  id: string;
  workspaceId: string;
  title: string;
  isAnonymous: true;
  candidates: SchedulePollCandidateDTO[];
  createdAt: string;
}

export interface CreateSchedulePollRequest {
  workspaceId: string;
  plannerId: string;
  title: string;
  candidates: SchedulePollCandidateRequest[];
}

export interface SchedulePollCandidateRequest {
  startDt: string;
  endDt: string;
}

export interface SchedulePollVoteSummaryDTO {
  startDt: string;
  endDt: string;
  voteCount: number;
}

const BASE = "/api/v1.0/schedule-polls";

export async function getSchedulePolls(workspaceId: string, plannerId: string): Promise<SchedulePollDTO[]> {
  const res = await apiFetch<SchedulePollDTO[]>(`${BASE}?workspaceId=${encodeURIComponent(workspaceId)}&plannerId=${encodeURIComponent(plannerId)}`);
  return res.data ?? [];
}

export async function getSchedulePollVoteSummary(workspaceId: string, plannerId: string): Promise<SchedulePollVoteSummaryDTO[]> {
  const res = await apiFetch<SchedulePollVoteSummaryDTO[]>(`${BASE}/vote-summary?workspaceId=${encodeURIComponent(workspaceId)}&plannerId=${encodeURIComponent(plannerId)}`);
  return res.data ?? [];
}

export async function createSchedulePoll(request: CreateSchedulePollRequest): Promise<SchedulePollDTO> {
  const res = await apiFetch<SchedulePollDTO>(BASE, {
    method: "POST",
    body: JSON.stringify(request),
  });
  return res.data;
}

export async function toggleSchedulePollVote(pollId: string, candidateId: string): Promise<SchedulePollDTO> {
  const res = await apiFetch<SchedulePollDTO>(`${BASE}/${pollId}/votes`, {
    method: "POST",
    body: JSON.stringify({ candidateId }),
  });
  return res.data;
}
