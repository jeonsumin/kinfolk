package com.terry.backend.api.workspace.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class WorkspaceStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "WORKSPACE";

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
        return "W%s%%04d";
    }
}
