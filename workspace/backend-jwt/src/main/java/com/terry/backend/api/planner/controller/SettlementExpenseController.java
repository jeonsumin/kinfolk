package com.terry.backend.api.planner.controller;

import com.terry.backend.api.planner.dto.CreateSettlementExpenseRequest;
import com.terry.backend.api.planner.dto.SettlementExpenseDTO;
import com.terry.backend.api.planner.service.SettlementExpenseService;
import com.terry.backend.web.controller.ApiRestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "SettlementExpense", description = "정산 내역 API")
public class SettlementExpenseController extends ApiRestController {

    private final SettlementExpenseService service;

    @GetMapping("/settlement-expenses")
    public List<SettlementExpenseDTO> getSettlementExpenses(@RequestParam String workspaceId,
                                                            @RequestParam String plannerId) throws Exception {
        return service.getSettlementExpenses(workspaceId, plannerId);
    }

    @PostMapping("/settlement-expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementExpenseDTO createSettlementExpense(@RequestBody CreateSettlementExpenseRequest request) throws Exception {
        return service.createSettlementExpense(request);
    }
}
