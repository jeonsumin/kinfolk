package com.terry.backend.api.admin.system.code.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class CodeStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "CODE";

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
        return "CODE%s%%04d";
    }
}
