package com.terry.backend.api.planner.service;

import com.terry.backend.api.planner.dto.CreateSettlementExpenseRequest;
import com.terry.backend.api.planner.dto.SettlementExpenseDTO;
import com.terry.backend.api.planner.mapper.SettlementExpenseMapper;
import com.terry.backend.api.planner.strategy.SettlementExpenseStrategy;
import com.terry.backend.core.excption.SystemException;
import com.terry.backend.core.security.util.SessionUtils;
import com.terry.backend.core.serial.config.SerialConfiguration;
import com.terry.backend.core.serial.util.SerialUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementExpenseService {

    private static final SerialConfiguration<String> STRATEGY = new SettlementExpenseStrategy();

    private final SettlementExpenseMapper mapper;

    private void checkMembership(String workspaceId, String userId) throws SystemException {
        if (!mapper.existsWorkspaceMember(workspaceId, userId)) {
            throw new SystemException(HttpStatus.FORBIDDEN, "접근권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<SettlementExpenseDTO> getSettlementExpenses(String workspaceId, String plannerId) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(workspaceId, userId);
        return mapper.selectSettlementExpenses(workspaceId, plannerId);
    }

    @Transactional
    public SettlementExpenseDTO createSettlementExpense(CreateSettlementExpenseRequest request) throws Exception {
        String userId = SessionUtils.getUserId();
        checkMembership(request.getWorkspaceId(), userId);
        String expenseId = SerialUtil.get(SettlementExpenseStrategy.ID, STRATEGY);
        LocalDate today = LocalDate.now();
        mapper.insertSettlementExpense(expenseId, request.getWorkspaceId(), request.getPlannerId(), today, request.getItem(), request.getPayer(), request.getAmount(), request.getStatus().name(), userId);
        return SettlementExpenseDTO.builder()
                .id(expenseId)
                .workspaceId(request.getWorkspaceId())
                .date(today)
                .item(request.getItem())
                .payer(request.getPayer())
                .amount(request.getAmount())
                .status(request.getStatus())
                .build();
    }
}
