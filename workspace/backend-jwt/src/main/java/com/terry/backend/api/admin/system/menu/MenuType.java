package com.terry.backend.api.admin.system.menu;

import com.terry.backend.api.code.dto.CodeDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MenuType {

    DIR("MenuType.DIR"),
    PAGE("MenuType.PAGE"),
    COMPONENT("MenuType.COMPONENT");

    private String code;

    private MenuType(String code) {
        this.code = code;
    }

    public static List<CodeDTO> toCodeList() {
        return Arrays
            .asList(MenuType.values()).stream().map(x -> CodeDTO
                .builder()
                .code(x.name())
                .name(x.toString())
                .build())
            .collect(Collectors.toList());
    }

}
