package com.terry.backend.api.planner.mapper;

import com.terry.backend.api.planner.dto.SchedulePollDTO;
import com.terry.backend.api.planner.dto.SchedulePollVoteSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchedulePollMapper {

    List<SchedulePollDTO> selectSchedulePolls(@Param("workspaceId") String workspaceId, @Param("plannerId") String plannerId, @Param("userId") String userId);

    List<SchedulePollVoteSummaryDTO> selectVoteSummary(@Param("workspaceId") String workspaceId, @Param("plannerId") String plannerId);

    void insertSchedulePoll(@Param("pollId") String pollId, @Param("wsId") String wsId, @Param("plannerId") String plannerId, @Param("title") String title, @Param("registId") String registId);

    void insertSchedulePollCandidate(@Param("candidateId") String candidateId, @Param("pollId") String pollId, @Param("startDt") String startDt, @Param("endDt") String endDt, @Param("registId") String registId);

    boolean existsVote(@Param("candidateId") String candidateId, @Param("userId") String userId);

    void insertVote(@Param("voteId") String voteId, @Param("candidateId") String candidateId, @Param("userId") String userId);

    void deleteVote(@Param("candidateId") String candidateId, @Param("userId") String userId);

    SchedulePollDTO selectSchedulePollById(@Param("pollId") String pollId, @Param("userId") String userId);

    boolean existsWorkspaceMember(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
}
