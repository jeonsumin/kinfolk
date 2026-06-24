package com.terry.backend.api.planner.service;

import com.terry.backend.api.planner.dto.CreateSchedulePollRequest;
import com.terry.backend.api.planner.dto.SchedulePollCandidateRequest;
import com.terry.backend.api.planner.dto.SchedulePollDTO;
import com.terry.backend.api.planner.dto.SchedulePollVoteSummaryDTO;
import com.terry.backend.api.planner.mapper.SchedulePollMapper;
import com.terry.backend.api.planner.strategy.SchedulePollCandidateStrategy;
import com.terry.backend.api.planner.strategy.SchedulePollStrategy;
import com.terry.backend.api.planner.strategy.SchedulePollVoteStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulePollService {

    private static final SerialConfiguration<String> POLL_STRATEGY      = new SchedulePollStrategy();
    private static final SerialConfiguration<String> CANDIDATE_STRATEGY = new SchedulePollCandidateStrategy();
    private static final SerialConfiguration<String> VOTE_STRATEGY      = new SchedulePollVoteStrategy();

    private final SchedulePollMapper mapper;

    private void checkMembership(String workspaceId, String userId) throws SystemException {
        if (!mapper.existsWorkspaceMember(workspaceId, userId)) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<SchedulePollDTO> getSchedulePolls(String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectSchedulePolls(workspaceId, userId);
    }

    @Transactional(readOnly = true)
    public List<SchedulePollVoteSummaryDTO> getVoteSummary(String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectVoteSummary(workspaceId);
    }

    @Transactional
    public SchedulePollDTO createSchedulePoll(CreateSchedulePollRequest request) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(request.getWorkspaceId(), userId);
        String pollId = SerialUtil.get(SchedulePollStrategy.ID, POLL_STRATEGY);
        mapper.insertSchedulePoll(pollId, request.getWorkspaceId(), request.getTitle(), userId);
        for (SchedulePollCandidateRequest c : request.getCandidates()) {
            String candidateId = SerialUtil.get(SchedulePollCandidateStrategy.ID, CANDIDATE_STRATEGY);
            mapper.insertSchedulePollCandidate(candidateId, pollId, c.getStartDt(), c.getEndDt(), userId);
        }
        return mapper.selectSchedulePollById(pollId, userId);
    }

    @Transactional
    public SchedulePollDTO toggleVote(String pollId, String candidateId) throws Exception {
        String userId = SessionUtils.getUserId();
        SchedulePollDTO poll = mapper.selectSchedulePollById(pollId, userId);
        if (poll == null) throw new IllegalArgumentException("존재하지 않는 투표입니다.");
        checkMembership(poll.getWorkspaceId(), userId);
        if (mapper.existsVote(candidateId, userId)) {
            mapper.deleteVote(candidateId, userId);
        } else {
            String voteId = SerialUtil.get(SchedulePollVoteStrategy.ID, VOTE_STRATEGY);
            mapper.insertVote(voteId, candidateId, userId);
        }
        return mapper.selectSchedulePollById(pollId, userId);
    }
}
