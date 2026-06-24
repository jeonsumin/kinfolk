package com.terry.backend.api.planner.controller;

import com.terry.backend.api.planner.dto.CreatePlannerRequest;
import com.terry.backend.api.planner.dto.PlannerDTO;
import com.terry.backend.api.planner.dto.UpdatePlannerItineraryRequest;
import com.terry.backend.api.planner.service.PlannerService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@Tag(name = "Planner", description = "플래너 API")
public class PlannerController extends ApiRestController {

    private final PlannerService service;

    @GetMapping("/planners")
    public List<PlannerDTO> getPlanners(@RequestParam String workspaceId) throws Exception {
        return service.getPlanners(workspaceId);
    }

    @GetMapping("/planners/{plannerId}")
    public PlannerDTO getPlanner(@PathVariable String plannerId,
                                 @RequestParam String workspaceId) throws Exception {
        return service.getPlanner(plannerId, workspaceId);
    }

    @PostMapping("/planners")
    @ResponseStatus(HttpStatus.CREATED)
    public PlannerDTO createPlanner(@RequestBody CreatePlannerRequest request) throws Exception {
        return service.createPlanner(request);
    }

    @PostMapping("/planners/{plannerId}/itinerary")
    public PlannerDTO updateItinerary(@PathVariable String plannerId,
                                      @RequestParam String workspaceId,
                                      @RequestBody UpdatePlannerItineraryRequest request) throws Exception {
        return service.updateItinerary(plannerId, workspaceId, request.getItinerary());
    }
}
