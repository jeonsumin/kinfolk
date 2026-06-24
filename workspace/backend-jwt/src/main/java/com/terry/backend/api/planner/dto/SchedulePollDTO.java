package com.terry.backend.api.planner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SchedulePollDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String workspaceId;
    private String title;
    @JsonProperty("isAnonymous")
    private boolean isAnonymous = true;
    private List<SchedulePollCandidateDTO> candidates;
    private LocalDateTime createdAt;
}
