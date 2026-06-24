package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SchedulePollCandidateDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDate startDt;
    private LocalDate endDt;
    private int voteCount;
    private boolean votedByMe;
}
