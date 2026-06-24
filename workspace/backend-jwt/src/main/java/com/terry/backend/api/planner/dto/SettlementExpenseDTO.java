package com.terry.backend.api.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SettlementExpenseDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String workspaceId;
    private LocalDate date;
    private String item;
    private String payer;
    private Long amount;
    private SettlementStatus status;
}
