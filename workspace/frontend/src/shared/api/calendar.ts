import {apiFetch, type ApiResponse} from "./client";

export type EventColor = "mauve" | "blue" | "green" | "neutral";
export type AttendeeStatus = "ACCEPTED" | "DECLINED" | "PENDING";

/** 백엔드 EventAttendeesDTO */
export interface AttendeeDTO {
    id: string;
    eventId: string;
    userId: string;
    status: AttendeeStatus;
    invitedAt?: string;
    updateDt?: string;
}

/** 백엔드 CalendarDTO (응답) */
export interface CalendarEventDTO {
    id: string;
    userId: string;       // 생성자
    wsId: string;
    title: string;
    description?: string;
    startDt: string;      // ISO 8601 or timestamp (Jackson 직렬화)
    endDt: string;
    isAllDay: number;     // 0 | 1
    location?: string;
    color: EventColor;
    registDt?: string;
    updateDt?: string;
    attendees: AttendeeDTO[];
}

/** POST /calendar 이벤트 필드 (event 필드 안에 들어가는 부분) */
export interface CreateEventPayload {
    wsId: string;
    title: string;
    description?: string;
    isAllDay: number;     // 0 | 1
    startDt: string;
    endDt: string;
    location?: string;
    color: EventColor;
}

/** POST /calendar 전체 요청 바디 (CalendarRequestDTO) */
export interface CalendarCreateRequest {
    event: CreateEventPayload;
    inviteeUserIds?: string[];
}

/** PUT /calendar/{id} 전체 요청 바디 (EventUpdateRequest) */
export interface CalendarUpdateRequest {
    event: CreateEventPayload;
    newInviteeUserIds?: string[];
    requestUserId: string;
}

const BASE = "/api/v1.0/calendar";

const fmt = (d: Date): string => d.toISOString().slice(0, 10); // YYYY-MM-DD

/** GET /api/v1.0/calendar/{workspaceId}/month */
export async function getMonthEvents(
    workspaceId: string,
    year: number,
    month: number
): Promise<ApiResponse<CalendarEventDTO[]>> {
    const start = fmt(new Date(year, month - 1, 1));
    const end = fmt(new Date(year, month, 0));
    return apiFetch<CalendarEventDTO[]>(
        `${BASE}/${workspaceId}/month?startOfMonth=${start}&endOfMonth=${end}`
    );
}

/** GET /api/v1.0/calendar/{workspaceId}/week */
export async function getWeekEvents(
    workspaceId: string,
    sunday: Date
): Promise<ApiResponse<CalendarEventDTO[]>> {
    const saturday = new Date(sunday);
    saturday.setDate(saturday.getDate() + 6);
    return apiFetch<CalendarEventDTO[]>(
        `${BASE}/${workspaceId}/week?startOfWeek=${fmt(sunday)}&endOfWeek=${fmt(saturday)}`
    );
}

/** GET /api/v1.0/calendar/{workspaceId}/day */
export async function getDayEvents(
    workspaceId: string,
    date: Date
): Promise<ApiResponse<CalendarEventDTO[]>> {
    return apiFetch<CalendarEventDTO[]>(
        `${BASE}/${workspaceId}/day?targetDate=${fmt(date)}`
    );
}

/** GET /api/v1.0/calendar/{id} */
export async function getCalendarEvent(
    id: string
): Promise<ApiResponse<CalendarEventDTO>> {
    return apiFetch<CalendarEventDTO>(`${BASE}/${id}`);
}

/** POST /api/v1.0/calendar */
export async function createCalendarEvent(
    request: CalendarCreateRequest
): Promise<ApiResponse<CalendarEventDTO>> {
    return apiFetch<CalendarEventDTO>(BASE, {
        method: "POST",
        body: JSON.stringify(request),
    });
}

/** PUT /api/v1.0/calendar/{id} */
export async function updateCalendarEvent(
    id: string,
    request: CalendarUpdateRequest
): Promise<ApiResponse<void>> {
    return apiFetch<void>(`${BASE}/${id}`, {
        method: "PUT",
        body: JSON.stringify(request),
    });
}

/** DELETE /api/v1.0/calendar/{id}?requestUserId= */
export async function deleteCalendarEvent(
    id: string,
    requestUserId: string
): Promise<ApiResponse<void>> {
    return apiFetch<void>(`${BASE}/${id}?requestUserId=${encodeURIComponent(requestUserId)}`, {
        method: "DELETE",
    });
}

/** PATCH /api/v1.0/calendar/{id}/attendee?userId=&status= */
export async function updateAttendeeStatus(
    id: string,
    userId: string,
    status: AttendeeStatus
): Promise<ApiResponse<void>> {
    return apiFetch<void>(
        `${BASE}/${id}/attendee?userId=${encodeURIComponent(userId)}&status=${encodeURIComponent(status)}`,
        {method: "PATCH"}
    );
}
