package com.terry.backend.api.shopping.strategy;

import com.terry.backend.core.serial.config.AbstractStringSerialConfiguration;

public class ShoppingCategoryStrategy extends AbstractStringSerialConfiguration {

    public static final String ID = "SHOPPING_CATEGORY";

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
        return "SC%s%%04d";
    }
}
