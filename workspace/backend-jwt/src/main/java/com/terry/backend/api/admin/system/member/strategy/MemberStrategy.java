package com.terry.backend.api.admin.system.member.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class MemberStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SWC_USER";

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
        return "U%s%%04d";
    }

}
