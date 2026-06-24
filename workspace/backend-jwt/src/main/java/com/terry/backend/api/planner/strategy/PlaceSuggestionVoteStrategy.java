package com.terry.backend.api.planner.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class PlaceSuggestionVoteStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "PLACE_SUGGESTION_VOTE";

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
        return "PSV%s%%04d";
    }
}
