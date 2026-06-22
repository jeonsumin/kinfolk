package com.terry.backend.api.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalendarDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String title;
    private String description;
    private Date startDt;
    private Date endDt;
    private int isAllDay;
    private String location;
    private String color;
    private Date registDt;
    private Date updateDt;
    private List<EventAttendeesDTO> attendees; // 참석자 목록 (1:N 관계)

}
