package com.terry.backend.api.admin.system.menu.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class MenuStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "MENU";

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
        return "M%s%%04d";
    }

}
