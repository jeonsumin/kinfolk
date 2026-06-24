import type { CreatePlannerRequest, PlannerDTO } from "./planner";

export const MOCK_WORKSPACE_ID = "mock-workspace";

export function createMockItineraryItem(itemCount = 0) {
  return {
    id: `activity-${Date.now()}`,
    time: `${String(Math.min(23, 9 + itemCount)).padStart(2, "0")}:00`,
    category: "일정",
    title: "새 일정",
    description: "일정 내용을 입력하세요.",
  };
}

export function createMockItineraryDay() {
  return {
    id: `day-${Date.now()}`,
    date: new Date().toISOString().slice(0, 10),
    title: "새로운 일정",
    items: [],
  };
}

const seedPlanners = (workspaceId: string): PlannerDTO[] => [
    {
      id: "jeju-friends-trip",
      workspaceId,
      title: "제주도 우정 여행",
      participants: ["이주", "민지", "준호", "서연"],
      color: "blue",
      updatedAt: "2026-06-24T09:00:00+09:00",
      calendar: { year: 2026, month: 10 },
      itinerary: [
        {
          id: "day-1",
          date: "2026-10-10",
          title: "도착 및 해변 탐방",
          items: [
            { id: "arrival", time: "09:30", category: "공항", title: "제주 국제 공항 도착", description: "일행과 합류한 뒤 렌터카 픽업 장소로 이동합니다." },
            { id: "lunch", time: "12:30", category: "식당", title: "현지 로컬 맛집 점심 식사", description: "함덕 근처에서 점심을 먹고 다음 장소로 이동합니다." },
          ],
        },
        {
          id: "day-2",
          date: "2026-10-11",
          title: "숲과 오름의 힐링",
          items: [
            { id: "forest", time: "10:00", category: "자연", title: "사려니 숲길 탐방", description: "삼나무 숲길을 따라 가볍게 산책합니다." },
          ],
        },
      ],
    },
    {
      id: "summer-family-vacation",
      workspaceId,
      title: "여름 가족 휴가",
      participants: ["엄마", "아빠", "지우"],
      color: "green",
      updatedAt: "2026-06-22T09:00:00+09:00",
      calendar: { year: 2026, month: 8 },
      itinerary: [],
    },
    {
      id: "year-end-gathering",
      workspaceId,
      title: "연말 모임",
      participants: ["은지", "도윤", "하린", "민석", "소희"],
      color: "mauve",
      updatedAt: "2026-06-18T09:00:00+09:00",
      calendar: { year: 2026, month: 12 },
      itinerary: [],
    },
  ];

const plannersByWorkspace = new Map<string, PlannerDTO[]>([
  [MOCK_WORKSPACE_ID, seedPlanners(MOCK_WORKSPACE_ID)],
]);

const clone = (planner: PlannerDTO): PlannerDTO => ({
  ...planner,
  participants: [...planner.participants],
  calendar: { ...planner.calendar },
  itinerary: planner.itinerary.map((day) => ({ ...day, items: day.items.map((item) => ({ ...item })) })),
});

function getList(workspaceId: string) {
  if (!plannersByWorkspace.has(workspaceId)) plannersByWorkspace.set(workspaceId, seedPlanners(workspaceId));
  return plannersByWorkspace.get(workspaceId)!;
}

export async function getMockPlanners(workspaceId: string): Promise<PlannerDTO[]> {
  return getList(workspaceId).map(clone);
}

export async function getMockPlanner(workspaceId: string, plannerId: string): Promise<PlannerDTO | null> {
  const planner = getList(workspaceId).find((item) => item.id === plannerId);
  return planner ? clone(planner) : null;
}

export async function createMockPlanner(request: CreatePlannerRequest): Promise<PlannerDTO> {
  const planner: PlannerDTO = {
    id: `${request.title.toLowerCase().replaceAll(/[^a-z0-9가-힣]+/g, "-")}-${Date.now()}`,
    workspaceId: request.workspaceId,
    title: request.title.trim(),
    participants: [...request.participants],
    color: "blue",
    updatedAt: new Date().toISOString(),
    calendar: { year: new Date().getFullYear(), month: new Date().getMonth() + 1 },
    itinerary: [],
  };
  getList(request.workspaceId).unshift(planner);
  return clone(planner);
}

export async function updateMockPlannerItinerary(
  workspaceId: string,
  plannerId: string,
  itinerary: PlannerDTO["itinerary"],
): Promise<PlannerDTO> {
  const planner = getList(workspaceId).find((item) => item.id === plannerId);
  if (!planner) throw new Error("Planner not found");
  planner.itinerary = itinerary.map((day) => ({ ...day, items: day.items.map((item) => ({ ...item })) }));
  planner.updatedAt = new Date().toISOString();
  return clone(planner);
}
