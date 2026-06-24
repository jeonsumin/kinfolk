package com.terry.backend.api.planner.mapper;

import com.terry.backend.api.planner.dto.PlannerDTO;
import com.terry.backend.api.planner.dto.PlannerItineraryDayDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlannerMapper {

    List<PlannerDTO> selectPlanners(@Param("workspaceId") String workspaceId);

    PlannerDTO selectPlannerById(@Param("plannerId") String plannerId);

    void insertPlanner(@Param("plannerId") String plannerId,
                       @Param("wsId") String wsId,
                       @Param("title") String title,
                       @Param("participants") List<String> participants,
                       @Param("color") String color,
                       @Param("calYear") int calYear,
                       @Param("calMonth") int calMonth,
                       @Param("itinerary") List<PlannerItineraryDayDTO> itinerary,
                       @Param("registId") String registId);

    void updatePlannerItinerary(@Param("plannerId") String plannerId,
                                @Param("itinerary") List<PlannerItineraryDayDTO> itinerary,
                                @Param("updtId") String updtId);

    boolean existsWorkspaceMember(@Param("workspaceId") String workspaceId,
                                  @Param("userId") String userId);
}
