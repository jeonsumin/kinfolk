package com.terry.backend.api.workspace.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class WorkspaceInvitationStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "WORKSPACE_INVITATION";

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
        return "WI%s%%04d";
    }
}
