package com.terry.backend.api.calendar.service;

import com.terry.backend.api.calendar.dto.CalendarDTO;
import com.terry.backend.api.calendar.dto.EventAttendeesDTO;
import com.terry.backend.api.calendar.mapper.CalendarMapper;
import com.terry.backend.core.security.util.SessionUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CalendarService {

    private final CalendarMapper mapper;

    public CalendarService(CalendarMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CalendarDTO> getWorkspaceEventsByMonth(String workspaceId, List<String> userIds, String startOfMonth, String endOfMonth) {
        return mapper.selectWorkspaceEventsByMonth(workspaceId, userIds, startOfMonth, endOfMonth);
    }

    @Transactional(readOnly = true)
    public List<CalendarDTO> getWorkspaceEventsByWeek(String workspaceId, List<String> userIds, String startOfWeek, String endOfWeek) {
        return mapper.selectWorkspaceEventsByWeek(workspaceId, userIds, startOfWeek, endOfWeek);
    }


    @Transactional(readOnly = true)
    public List<CalendarDTO> getWorkspaceEventsByDay(String workspaceId, List<String> userIds, String targetDate) {
        return mapper.selectWorkspaceEventsByDay(workspaceId, userIds, targetDate);
    }


    @Transactional(readOnly = true)
    public CalendarDTO getEventDetail(String id) {
        CalendarDTO event = mapper.selectEventDetail(id);
        if (event == null) {
            throw new IllegalArgumentException("존재하지 않는 일정입니다.");
        }
        return event;
    }


    @Transactional
    public void createEvent(CalendarDTO event, List<String> inviteeUserIds) {
        // 1. String 타입 고유 ID 생성 (UUID 활용)
        String eventId = UUID.randomUUID().toString();
        event.setId(eventId);

        event.setUserId(SessionUtils.getUserId());

        // 2. 일정 기본 정보 저장
        mapper.insertEvent(event);

        // 3. 주최자 본인을 'ACCEPTED'(수락) 상태로 참석자 목록에 자동 추가
        EventAttendeesDTO eventAttendeesDTO = EventAttendeesDTO.builder()
                .eventId(eventId)
                .userId(event.getUserId())
                .status("ACCEPTED")
                .build();

        mapper.insertAttendee(eventAttendeesDTO);

        // 4. 초대받은 다른 팀원들을 'PENDING'(대기) 상태로 추가
        if (inviteeUserIds != null) {
            for (String inviteeId : inviteeUserIds) {
                // 주최자가 중복 초대되는 것 방지
                if (!inviteeId.equals(event.getUserId())) {
                    eventAttendeesDTO.setUserId(inviteeId);
                    eventAttendeesDTO.setStatus("PENDING");
                    mapper.insertAttendee(eventAttendeesDTO);
                }
            }
        }
    }


    @Transactional
    public void modifyEvent(CalendarDTO event, String requestUserId, List<String> newInviteeUserIds) {
        // 1. 기존 일정 조회 및 권한 검증 (주최자만 수정 가능)
        CalendarDTO originalEvent = getEventDetail(event.getId());
        if (!originalEvent.getUserId().equals(requestUserId)) {
            throw new SecurityException("일정 주최자만 수정할 수 있습니다.");
        }

        // 2. 일정 기본 정보 수정
        mapper.updateEvent(event);

        // 3. 참석자 명단 갱신 (기존 명단 비우고 새로 매핑)
        mapper.deleteAttendeesByEventId(event.getId());

        // 4. 주최자 및 새 초대 명단 재등록
        EventAttendeesDTO eventAttendeesDTO = EventAttendeesDTO.builder()
                .eventId(event.getId())
                .userId(originalEvent.getUserId())
                .status("ACCEPTED")
                .build();
        mapper.insertAttendee(eventAttendeesDTO);
        if (newInviteeUserIds != null) {
            for (String inviteeId : newInviteeUserIds) {
                if (!inviteeId.equals(originalEvent.getUserId())) {
                    eventAttendeesDTO = EventAttendeesDTO.builder()
                            .eventId(event.getId())
                            .userId(inviteeId)
                            .status("PENDING")
                            .build();
                    mapper.insertAttendee(eventAttendeesDTO);
                }
            }
        }
    }


    @Transactional
    public void removeEvent(String id, String requestUserId) {
        // 1. 권한 검증 (주최자만 삭제 가능)
        CalendarDTO originalEvent = getEventDetail(id);
        if (!originalEvent.getUserId().equals(requestUserId)) {
            throw new SecurityException("일정 주최자만 삭제할 수 있습니다.");
        }

        // 2. 일정 삭제 (EVENT_ATTENDEES는 DB 외래키 제약조건에 의해 자동 연쇄 삭제됨)
        mapper.deleteEvent(id);
    }


    public void changeAttendeeStatus(String eventId, String userId, String status) {
        // 참석자가 자신의 상태(수락/거절 등)를 변경
        EventAttendeesDTO eventAttendeesDTO = EventAttendeesDTO.builder()
                .eventId(eventId)
                .userId(userId)
                .status(status)
                .build();
        int updatedRows = mapper.updateAttendeeStatus(eventAttendeesDTO);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("해당 일정의 참석자 명단에 존재하지 않는 사용자입니다.");
        }
    }

}
