package com.terry.backend.api.admin.system.authority.dto;

import com.terry.backend.core.dto.BaseSearchParam;
import com.terry.backend.web.security.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthoritySearchParam extends BaseSearchParam {

    private String   id;
    private RoleType code;
    private String   name;
    private String   use;

}
