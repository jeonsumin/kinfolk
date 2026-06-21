package com.terry.backend.core.serial.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Data
public class CoreSerialConfig  implements SerialConfiguration<String>{
    private String compareDateFormat = "yyyyMMddHHmm";
    private String dateFormat = "yyyyMMddHHmm";
    private String formatString = "%s%%06d";

    @Override
    public String getSerial(Integer value) {
        final DateFormat df = new SimpleDateFormat(getDateFormat());
        String format = String.format(formatString, df.format(new Date()));

        return String.format(format,value);
    }
}
