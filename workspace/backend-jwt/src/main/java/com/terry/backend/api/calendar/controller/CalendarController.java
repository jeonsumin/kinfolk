package com.terry.backend.api.calendar.controller;

import com.terry.backend.api.calendar.dto.CalendarDTO;
import com.terry.backend.api.calendar.dto.CalendarRequestDTO;
import com.terry.backend.api.calendar.dto.EventUpdateRequest;
import com.terry.backend.api.calendar.service.CalendarService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Calendar", description = "캘린더 API")
public class CalendarController extends ApiRestController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    @GetMapping("/calendar/{workspaceId}/month")
    public List<CalendarDTO> getMonthEvents(
            @PathVariable String workspaceId,
            @RequestParam(required = false) List<String> userIds,
            @RequestParam String startOfMonth,
            @RequestParam String endOfMonth) {
        return service.getWorkspaceEventsByMonth(workspaceId, userIds, startOfMonth, endOfMonth);
    }

    @GetMapping("/calendar/{workspaceId}/week")
    public List<CalendarDTO> getWeekEvents(
            @PathVariable String workspaceId,
            @RequestParam(required = false) List<String> userIds,
            @RequestParam String startOfWeek,
            @RequestParam String endOfWeek) {
        return service.getWorkspaceEventsByWeek(workspaceId, userIds, startOfWeek, endOfWeek);
    }

    @GetMapping("/calendar/{workspaceId}/day")
    public List<CalendarDTO> getDayEvents(
            @PathVariable String workspaceId,
            @RequestParam(required = false) List<String> userIds,
            @RequestParam String targetDate) {
        return service.getWorkspaceEventsByDay(workspaceId, userIds, targetDate);
    }

    @GetMapping("/calendar/{id}")
    public CalendarDTO getEventDetail(@PathVariable String id) {
        return service.getEventDetail(id);
    }

    @PostMapping("/calendar")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEvent(@RequestBody CalendarRequestDTO request) {
        service.createEvent(request.getEvent(), request.getInviteeUserIds());
    }

    @PutMapping("/calendar/{id}")
    public void modifyEvent(@PathVariable String id, @RequestBody EventUpdateRequest request) {
        request.getEvent().setId(id);
        service.modifyEvent(request.getEvent(), request.getRequestUserId(), request.getNewInviteeUserIds());
    }

    @DeleteMapping("/calendar/{id}")
    public void removeEvent(@PathVariable String id, @RequestParam String requestUserId) {
        service.removeEvent(id, requestUserId);
    }

    @PatchMapping("/calendar/{id}/attendee")
    public void changeAttendeeStatus(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestParam String status) {
        service.changeAttendeeStatus(id, userId, status);
    }
}
