package com.terry.backend.api.calendar.mapper;

import com.terry.backend.api.calendar.dto.CalendarDTO;
import com.terry.backend.api.calendar.dto.EventAttendeesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarMapper {

    /**
     * 1. 워크스페이스 팀원 월간 조회
     *
     * @param workspaceId  워크스페이스 ID
     * @param userIds      필터링할 유저 ID 목록 (null이거나 비어있으면 전체조회)
     * @param startOfMonth 월 시작일 (예: "2026-06-01")
     * @param endOfMonth   월 종료일 (예: "2026-06-30")
     */
    List<CalendarDTO> selectWorkspaceEventsByMonth(
            @Param("workspaceId") String workspaceId,
            @Param("userIds") List<String> userIds,
            @Param("startOfMonth") String startOfMonth,
            @Param("endOfMonth") String endOfMonth
    );

    /**
     * 2. 워크스페이스 팀원 주간 조회
     *
     * @param workspaceId 워크스페이스 ID
     * @param userIds     필터링할 유저 ID 목록
     * @param startOfWeek 주 시작일 (예: "2026-06-22")
     * @param endOfWeek   주 종료일 (예: "2026-06-28")
     */
    List<CalendarDTO> selectWorkspaceEventsByWeek(
            @Param("workspaceId") String workspaceId,
            @Param("userIds") List<String> userIds,
            @Param("startOfWeek") String startOfWeek,
            @Param("endOfWeek") String endOfWeek
    );

    /**
     * 3. 워크스페이스 팀원 일간 조회
     *
     * @param workspaceId 워크스페이스 ID
     * @param userIds     필터링할 유저 ID 목록
     * @param targetDate  조회할 날짜 (예: "2026-06-22")
     */
    List<CalendarDTO> selectWorkspaceEventsByDay(
            @Param("workspaceId") String workspaceId,
            @Param("userIds") List<String> userIds,
            @Param("targetDate") String targetDate
    );

    /**
     * 4. 특정 일정 단건 상세 조회
     *
     * @param id 일정 고유 식별자 (ID)
     */
    CalendarDTO selectEventDetail(@Param("id") String id);

    // 새 일정 생성
    void insertEvent(CalendarDTO event);

    // 일정 상세 수정
    void updateEvent(CalendarDTO event);

    // 일정 삭제
    void deleteEvent(@Param("id") String id);

    // 일정에 참석자 추가
    void insertAttendee(EventAttendeesDTO entity);

    // 특정 일정의 참석자 전원 삭제 (수정 시 초기화 용도)
    void deleteAttendeesByEventId(@Param("eventId") String eventId);

    // 참석 상태 변경 (수락/거절 등)
    int updateAttendeeStatus(EventAttendeesDTO entity);
}
