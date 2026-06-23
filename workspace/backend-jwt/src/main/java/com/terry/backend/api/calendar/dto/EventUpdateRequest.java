package com.terry.backend.api.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {

    private CalendarDTO event;
    private List<String> newInviteeUserIds;
    private String requestUserId;
}
