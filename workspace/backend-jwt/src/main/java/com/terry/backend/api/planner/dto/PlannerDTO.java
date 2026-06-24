package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class PlannerDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String workspaceId;
    private String title;
    private List<String> participants;
    private String color;
    private LocalDateTime updatedAt;
    private PlannerCalendarDTO calendar;
    private List<PlannerItineraryDayDTO> itinerary;
}
