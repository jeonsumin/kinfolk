package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class PlaceSuggestionDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String workspaceId;
    private String sourceUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private String category;
    private int voteCount;
    private boolean votedByMe;
}
