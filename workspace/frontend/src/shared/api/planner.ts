export type PlannerColor = "blue" | "green" | "mauve";

export interface PlannerCalendarDTO {
  year: number;
  month: number;
}

export interface PlannerItineraryItemDTO {
  id: string;
  time: string;
  category: string;
  title: string;
  description: string;
}

export interface PlannerItineraryDayDTO {
  id: string;
  date: string;
  title: string;
  items: PlannerItineraryItemDTO[];
}

/** 플래너 목록·상세 API가 반환할 데이터 계약입니다. */
export interface PlannerDTO {
  id: string;
  workspaceId: string;
  title: string;
  participants: string[];
  color: PlannerColor;
  updatedAt: string;
  calendar: PlannerCalendarDTO;
  itinerary: PlannerItineraryDayDTO[];
}

/** POST /api/v1.0/planners 요청 바디입니다. */
export interface CreatePlannerRequest {
  workspaceId: string;
  title: string;
  participants: string[];
}

const BASE = "/api/v1.0/planners";

/** GET /api/v1.0/planners?workspaceId={workspaceId} */
export async function getPlanners(workspaceId: string): Promise<PlannerDTO[]> {
  const res = await apiFetch<PlannerDTO[]>(`${BASE}?workspaceId=${encodeURIComponent(workspaceId)}`);
  return res.data ?? [];
}

/** GET /api/v1.0/planners/{plannerId}?workspaceId={workspaceId} */
export async function getPlanner(workspaceId: string, plannerId: string): Promise<PlannerDTO> {
  const res = await apiFetch<PlannerDTO>(`${BASE}/${plannerId}?workspaceId=${encodeURIComponent(workspaceId)}`);
  return res.data;
}

/** POST /api/v1.0/planners */
export async function createPlanner(request: CreatePlannerRequest): Promise<PlannerDTO> {
  const res = await apiFetch<PlannerDTO>(BASE, {
    method: "POST",
    body: JSON.stringify(request),
  });
  return res.data;
}

/** POST /api/v1.0/planners/{plannerId}/itinerary */
export async function updatePlannerItinerary(
  workspaceId: string,
  plannerId: string,
  itinerary: PlannerItineraryDayDTO[],
): Promise<PlannerDTO> {
  const res = await apiFetch<PlannerDTO>(`${BASE}/${plannerId}/itinerary?workspaceId=${encodeURIComponent(workspaceId)}`, {
    method: "POST",
    body: JSON.stringify({ itinerary }),
  });
  return res.data;
}
import { apiFetch } from "./client";
