package com.terry.backend.web.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuAuthority {

    private static final int READ = 0x01;
    private static final int WRITE = 0x02;
    private static final int DELETE = 0x04;

    @JsonIgnore
    private RoleType authority;

    @JsonIgnore
    private int authorityValue;

    public boolean isRead() {
        return (authorityValue & READ) != READ;
    }

    public boolean isWrite() {
        return (authorityValue & WRITE) != WRITE;
    }

    public boolean isDelete() {
        return (authorityValue & DELETE) != DELETE;
    }
}
