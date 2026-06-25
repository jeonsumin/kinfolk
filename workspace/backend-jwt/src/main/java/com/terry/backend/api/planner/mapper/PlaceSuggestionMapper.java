package com.terry.backend.api.planner.mapper;

import com.terry.backend.api.planner.dto.PlaceSuggestionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaceSuggestionMapper {

    List<PlaceSuggestionDTO> selectPlaceSuggestions(@Param("workspaceId") String workspaceId, @Param("plannerId") String plannerId, @Param("userId") String userId);

    void insertPlaceSuggestion(@Param("placeId") String placeId, @Param("wsId") String wsId, @Param("plannerId") String plannerId, @Param("sourceUrl") String sourceUrl, @Param("thumbnailUrl") String thumbnailUrl, @Param("title") String title, @Param("description") String description, @Param("category") String category, @Param("registId") String registId);

    boolean existsPlaceVote(@Param("placeId") String placeId, @Param("userId") String userId);

    void insertPlaceVote(@Param("voteId") String voteId, @Param("placeId") String placeId, @Param("userId") String userId);

    void deletePlaceVote(@Param("placeId") String placeId, @Param("userId") String userId);

    PlaceSuggestionDTO selectPlaceSuggestionById(@Param("placeId") String placeId, @Param("userId") String userId);

    boolean existsWorkspaceMember(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
}
