package com.terry.backend.api.planner.service;

import com.terry.backend.api.planner.dto.CreatePlaceSuggestionRequest;
import com.terry.backend.api.planner.dto.PlaceSuggestionDTO;
import com.terry.backend.api.planner.mapper.PlaceSuggestionMapper;
import com.terry.backend.api.planner.strategy.PlaceSuggestionStrategy;
import com.terry.backend.api.planner.strategy.PlaceSuggestionVoteStrategy;
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
public class PlaceSuggestionService {

    private static final SerialConfiguration<String> STRATEGY      = new PlaceSuggestionStrategy();
    private static final SerialConfiguration<String> VOTE_STRATEGY = new PlaceSuggestionVoteStrategy();

    private final PlaceSuggestionMapper mapper;

    private void checkMembership(String workspaceId, String userId) throws SystemException {
        if (!mapper.existsWorkspaceMember(workspaceId, userId)) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<PlaceSuggestionDTO> getPlaceSuggestions(String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectPlaceSuggestions(workspaceId, userId);
    }

    @Transactional
    public PlaceSuggestionDTO createPlaceSuggestion(CreatePlaceSuggestionRequest request) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(request.getWorkspaceId(), userId);
        String placeId = SerialUtil.get(PlaceSuggestionStrategy.ID, STRATEGY);
        mapper.insertPlaceSuggestion(placeId, request.getWorkspaceId(), request.getSourceUrl(), request.getThumbnailUrl(), request.getTitle(), request.getDescription(), request.getCategory(), userId);
        return mapper.selectPlaceSuggestionById(placeId, userId);
    }

    @Transactional
    public PlaceSuggestionDTO toggleVote(String placeId) throws Exception {
        String userId = SessionUtils.getUserId();
        PlaceSuggestionDTO suggestion = mapper.selectPlaceSuggestionById(placeId, userId);
        if (suggestion == null) throw new IllegalArgumentException("존재하지 않는 장소 제안입니다.");
        checkMembership(suggestion.getWorkspaceId(), userId);
        if (mapper.existsPlaceVote(placeId, userId)) {
            mapper.deletePlaceVote(placeId, userId);
        } else {
            String voteId = SerialUtil.get(PlaceSuggestionVoteStrategy.ID, VOTE_STRATEGY);
            mapper.insertPlaceVote(voteId, placeId, userId);
        }
        return mapper.selectPlaceSuggestionById(placeId, userId);
    }
}
