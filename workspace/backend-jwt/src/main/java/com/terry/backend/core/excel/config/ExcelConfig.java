package com.terry.backend.core.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("backend.excel")
@Data
public class ExcelConfig {
    private String contextPath = "/excel";
    private String basePath = "classpath:excel/";
    private String temporaryPath = "./excel_temporary/";
}
