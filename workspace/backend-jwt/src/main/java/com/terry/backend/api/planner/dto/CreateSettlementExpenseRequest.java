package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSettlementExpenseRequest {

    private String workspaceId;
    private String item;
    private String payer;
    private Long amount;
    private SettlementStatus status;
}
