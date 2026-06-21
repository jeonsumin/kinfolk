package com.terry.backend.web.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("backend.file")
@Data
public class FileConfig {

    private String basename          = "./upload";
    private String delim             = "/temp/";
    private String format            = "F%s%%06d";
    private String dateFormat        = "yyyyMMddHHmm";
    private String compareDateFormat = "yyyyMMddHHmm";
}
