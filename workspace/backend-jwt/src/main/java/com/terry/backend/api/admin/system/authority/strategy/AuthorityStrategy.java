package com.terry.backend.api.admin.system.authority.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class AuthorityStrategy extends AbstractStringSerialConfiguration {

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
        return "A%s%%04d";
    }

}
