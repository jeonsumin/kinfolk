package com.terry.backend.api.planner.mapper;

import com.terry.backend.api.planner.dto.SettlementExpenseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SettlementExpenseMapper {

    List<SettlementExpenseDTO> selectSettlementExpenses(@Param("workspaceId") String workspaceId);

    void insertSettlementExpense(@Param("expenseId") String expenseId, @Param("wsId") String wsId, @Param("expenseDate") LocalDate expenseDate, @Param("item") String item, @Param("payer") String payer, @Param("amount") Long amount, @Param("status") String status, @Param("registId") String registId);

    boolean existsWorkspaceMember(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
}
