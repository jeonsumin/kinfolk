package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UpdatePlannerItineraryRequest {
    private static final long serialVersionUID = 1L;

    private List<PlannerItineraryDayDTO> itinerary;
}
