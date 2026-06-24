package com.terry.backend.api.planner.service;

import com.terry.backend.api.planner.dto.CreatePlannerRequest;
import com.terry.backend.api.planner.dto.PlannerDTO;
import com.terry.backend.api.planner.dto.PlannerItineraryDayDTO;
import com.terry.backend.api.planner.mapper.PlannerMapper;
import com.terry.backend.api.planner.strategy.PlannerStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannerService {

    private static final SerialConfiguration<String> STRATEGY = new PlannerStrategy();

    private final PlannerMapper mapper;

    private void checkMembership(String workspaceId, String userId) throws SystemException {
        if (!mapper.existsWorkspaceMember(workspaceId, userId)) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<PlannerDTO> getPlanners(String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectPlanners(workspaceId);
    }

    @Transactional(readOnly = true)
    public PlannerDTO getPlanner(String plannerId, String workspaceId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        PlannerDTO planner = mapper.selectPlannerById(plannerId);
        if (planner == null) {
            throw new IllegalArgumentException("존재하지 않는 플래너입니다.");
        }
        return planner;
    }

    @Transactional
    public PlannerDTO createPlanner(CreatePlannerRequest request) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(request.getWorkspaceId(), userId);
        String plannerId = SerialUtil.get(PlannerStrategy.ID, STRATEGY);
        LocalDate now = LocalDate.now();
        List<PlannerItineraryDayDTO> emptyItinerary = new ArrayList<>();
        mapper.insertPlanner(plannerId, request.getWorkspaceId(), request.getTitle(),
                request.getParticipants(), "blue", now.getYear(), now.getMonthValue(),
                emptyItinerary, userId);
        return mapper.selectPlannerById(plannerId);
    }

    @Transactional
    public PlannerDTO updateItinerary(String plannerId, String workspaceId, List<PlannerItineraryDayDTO> itinerary) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        PlannerDTO planner = mapper.selectPlannerById(plannerId);
        if (planner == null) {
            throw new IllegalArgumentException("존재하지 않는 플래너입니다.");
        }
        mapper.updatePlannerItinerary(plannerId, itinerary, userId);
        return mapper.selectPlannerById(plannerId);
    }
}
