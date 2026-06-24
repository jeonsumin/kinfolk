package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaceSuggestionRequest {

    private String workspaceId;
    private String sourceUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private String category;
}
