package com.terry.backend.api.planner.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class PlaceSuggestionStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "PLACE_SUGGESTION";

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
        return "PS%s%%04d";
    }
}
