package com.terry.backend.api.admin.system.code;

import com.terry.backend.api.code.dto.CodeDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CodeType {

    SYSTEM("CodeType.SYSTEM"),
    USER("CodeType.USER");

    private String messageCode;

    private CodeType(String messageCode) {
        this.messageCode = messageCode;
    }

    public static List<CodeDTO> toCodeList() {
        return Arrays
                .asList(CodeType.values()).stream()
                .map(x -> CodeDTO
                        .builder()
                        .code(x.name())
                        .name(x.toString())
                        .build())
                .collect(Collectors.toList());
    }

}
