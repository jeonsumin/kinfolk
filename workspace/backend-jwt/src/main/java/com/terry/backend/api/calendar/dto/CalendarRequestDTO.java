package com.terry.backend.api.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRequestDTO {

    private CalendarDTO event;
    private List<String> inviteeUserIds;
    private List<String> newInviteeUserIds;
    private String requestUserId;
}
