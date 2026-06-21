package com.terry.backend.web.security;

import com.terry.backend.api.code.dto.CodeDTO;
import com.terry.backend.core.messages.util.MessageSourceUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum RoleType {

    ADMIN("RoleType.ADMIN", true),
    SUPER("RoleType.SUPER", true),
    USER("RoleType.USER", false);

    private final String localeCode;

    @Getter
    private final boolean admin;

    private RoleType(String localeCode, boolean admin){
        this.localeCode = localeCode;
        this.admin = admin;
    }

    @Override
    public String toString(){
        return MessageSourceUtils.getMessage(localeCode);
    }

    public static List<CodeDTO> toCodeList() {
        return Arrays
                .asList(RoleType.values()).stream()
                .map(x -> CodeDTO.builder().code(x.name()).name(x.toString()).build())
                .collect(Collectors.toList());
    }

    public static List<CodeDTO> toCodeList(boolean admin) {
        return Arrays
                .asList(RoleType.values()).stream()
                .filter(x -> x.isAdmin() == admin)
                .map(x -> CodeDTO.builder().code(x.name()).name(x.toString()).build())
                .collect(Collectors.toList());
    }
}
