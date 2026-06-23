package com.terry.backend.api.shopping.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class ShoppingItemStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SHOPPING_ITEM";

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
        return "SI%s%%04d";
    }
}
