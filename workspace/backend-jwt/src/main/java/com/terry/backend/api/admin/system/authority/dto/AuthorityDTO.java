package com.terry.backend.api.admin.system.authority.dto;


import com.terry.backend.core.dto.BaseDTO;
import com.terry.backend.web.security.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthorityDTO extends BaseDTO {

    private static final long serialVersionUID = -5335593579923164409L;

    private String                   id;
    @NotNull
    private RoleType                code;
    private String                   name;
    private String                   description;
    private String                   use;
    private List<AuthorityMemberDTO> members;
    private List<AuthorityMenuDTO>   menues;
    private int                      authorUserCount;

}
