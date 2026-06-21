package com.terry.backend.core.serial.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractStringSerialConfiguration implements SerialConfiguration<String>{

    @Override
    public String getSerial(Integer value) {
        final DateFormat df = new SimpleDateFormat(getDateFormat());
        final String format = String.format(getFormatString(), df.format(new Date()));
        return String.format(format, value);
    }
}
