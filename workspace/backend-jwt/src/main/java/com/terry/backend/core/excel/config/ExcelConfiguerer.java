package com.terry.backend.core.excel.config;

import com.terry.backend.core.excel.mapper.ExcelMapper;
import com.terry.backend.core.excel.util.ExcelUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcelConfiguerer {
    private final ExcelConfig config;
    private final ExcelMapper excelMapper;

    public ExcelConfiguerer(
            ExcelConfig config,
            ExcelMapper excelMapper
    ) {
        this.config = config;
        this.excelMapper = excelMapper;
    }

    @Bean
    public ExcelUtils excelUtils() throws Exception {
        ExcelUtils utils = new ExcelUtils();
        utils.setConfig(config);
        utils.setMapper(excelMapper);
        utils.afterPropertiesSet();

        return utils;
    }
}
