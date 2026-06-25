package com.terry.backend.api.planner.controller;

import com.terry.backend.api.planner.dto.CreateSchedulePollRequest;
import com.terry.backend.api.planner.dto.SchedulePollDTO;
import com.terry.backend.api.planner.dto.SchedulePollVoteSummaryDTO;
import com.terry.backend.api.planner.dto.TogglePollVoteRequest;
import com.terry.backend.api.planner.service.SchedulePollService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "SchedulePoll", description = "희망일정 투표 API")
public class SchedulePollController extends ApiRestController {

    private final SchedulePollService service;

    @GetMapping("/schedule-polls")
    public List<SchedulePollDTO> getSchedulePolls(@RequestParam String workspaceId,
                                                  @RequestParam String plannerId) throws Exception {
        return service.getSchedulePolls(workspaceId, plannerId);
    }

    @GetMapping("/schedule-polls/vote-summary")
    public List<SchedulePollVoteSummaryDTO> getVoteSummary(@RequestParam String workspaceId,
                                                           @RequestParam String plannerId) throws Exception {
        return service.getVoteSummary(workspaceId, plannerId);
    }

    @PostMapping("/schedule-polls")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedulePollDTO createSchedulePoll(@Valid @RequestBody CreateSchedulePollRequest request) throws Exception {
        return service.createSchedulePoll(request);
    }

    @PostMapping("/schedule-polls/{pollId}/votes")
    public SchedulePollDTO toggleVote(@PathVariable String pollId, @RequestBody TogglePollVoteRequest request) throws Exception {
        return service.toggleVote(pollId, request.getCandidateId());
    }
}
