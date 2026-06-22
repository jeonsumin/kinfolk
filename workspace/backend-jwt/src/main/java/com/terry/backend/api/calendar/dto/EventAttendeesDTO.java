package com.terry.backend.api.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EventAttendeesDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String eventId;
    private String userId;
    private AttendeesType status;
    private Date invitedAt;
    private Date updateDt;
}
