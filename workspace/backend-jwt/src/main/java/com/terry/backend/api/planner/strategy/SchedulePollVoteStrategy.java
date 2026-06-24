package com.terry.backend.api.planner.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class SchedulePollVoteStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SCHEDULE_POLL_VOTE";

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
        return "SPV%s%%04d";
    }
}
