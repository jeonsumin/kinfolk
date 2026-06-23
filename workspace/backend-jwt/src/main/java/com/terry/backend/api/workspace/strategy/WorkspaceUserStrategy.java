package com.terry.backend.api.workspace.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class WorkspaceUserStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "WORKSPACE_USER";

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
        return "WU%s%%04d";
    }
}
