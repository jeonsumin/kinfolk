package com.terry.backend.api.planner.controller;

import com.terry.backend.api.planner.dto.CreatePlaceSuggestionRequest;
import com.terry.backend.api.planner.dto.PlacePreviewDTO;
import com.terry.backend.api.planner.dto.PlaceSuggestionDTO;
import com.terry.backend.api.planner.service.PlacePreviewService;
import com.terry.backend.api.planner.service.PlaceSuggestionService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "PlaceSuggestion", description = "장소 제안 API")
public class PlaceSuggestionController extends ApiRestController {

    private final PlaceSuggestionService service;
    private final PlacePreviewService previewService;

    @GetMapping("/place-suggestions")
    public List<PlaceSuggestionDTO> getPlaceSuggestions(@RequestParam String workspaceId) throws Exception {
        return service.getPlaceSuggestions(workspaceId);
    }

    @GetMapping("/place-suggestions/preview")
    public PlacePreviewDTO resolvePreview(@RequestParam String url) {
        return previewService.resolvePreview(url);
    }

    @PostMapping("/place-suggestions")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceSuggestionDTO createPlaceSuggestion(@RequestBody CreatePlaceSuggestionRequest request) throws Exception {
        return service.createPlaceSuggestion(request);
    }

    @PostMapping("/place-suggestions/{placeId}/votes")
    public PlaceSuggestionDTO toggleVote(@PathVariable String placeId) throws Exception {
        return service.toggleVote(placeId);
    }
}
