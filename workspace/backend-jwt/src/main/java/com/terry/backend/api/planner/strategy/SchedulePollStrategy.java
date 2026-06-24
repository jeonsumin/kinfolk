package com.terry.backend.api.planner.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class SchedulePollStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SCHEDULE_POLL";

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
        return "SP%s%%04d";
    }
}
