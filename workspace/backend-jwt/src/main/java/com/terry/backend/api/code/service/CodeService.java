package com.terry.backend.api.code.service;

import com.terry.backend.api.admin.system.code.strategy.CodeStrategy;
import com.terry.backend.api.code.dto.CodeDTO;
import com.terry.backend.api.code.mapper.CodeMapper;
import com.terry.backend.core.serial.config.SerialConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeService {

    private final CodeMapper mapper;
    private static final SerialConfiguration<String> ID_STRATEGY = new CodeStrategy();

    public CodeService(
            CodeMapper mapper
    ) {
        this.mapper = mapper;
    }

    public List<CodeDTO> findByPath(final String path) throws Exception {
        return mapper.findByPath(path);
    }
}
