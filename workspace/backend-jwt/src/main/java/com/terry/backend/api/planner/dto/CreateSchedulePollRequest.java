package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSchedulePollRequest {

    private String workspaceId;
    private String title;
    private List<SchedulePollCandidateRequest> candidates;
}
