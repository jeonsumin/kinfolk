package com.terry.backend.web.security;


import com.terry.backend.api.code.dto.CodeDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AuthorityValue {

    READ("AuthorityValue.READ", 0x01, 0x01),
    WRITE("AuthorityValue.WRITE", 0x02, 0x03),
    DELETE("AuthorityValue.DELETE", 0x04, 0x07);

    private String labelCode;
    private int    value;
    private int    offset;

    private AuthorityValue(String labelCode, int value, int offset) {
        this.labelCode = labelCode;
        this.value = value;
        this.offset = offset;
    }

    public boolean test(int value) {
        return (value & getValue()) == getValue();
    }

    public int getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    public static List<CodeDTO> toCodeList() {
        return Arrays
            .asList(AuthorityValue.values())
            .stream()
            .map(x -> CodeDTO
                .builder()
                .code(x.name())
                .name(x.toString())
                .value1(String.valueOf(x.value))
                .value2(String.valueOf(x.offset))
                .build())
            .collect(Collectors.toList());
    }

    public static boolean is(AuthorityValue authority, int value) {
        return (value & authority.getValue()) == authority.getValue();
    }

}
