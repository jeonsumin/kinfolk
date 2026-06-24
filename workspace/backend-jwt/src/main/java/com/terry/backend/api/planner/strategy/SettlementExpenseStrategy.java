package com.terry.backend.api.planner.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class SettlementExpenseStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SETTLEMENT_EXPENSE";

    @Override
    public String getDateFormat() {
        return "yyyyMMddHHmm";
    }

    @Override
    public String getCompareDateFormat() {
        return "yyyyMMddHHmm";
    }

    @Override
    public String getFormatString() {
        return "SE%s%%04d";
    }
}
