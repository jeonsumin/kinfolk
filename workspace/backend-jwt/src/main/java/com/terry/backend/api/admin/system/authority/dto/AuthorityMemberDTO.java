package com.terry.backend.api.admin.system.authority.dto;

import com.terry.backend.core.security.dto.UserDTO;
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
public class AuthorityMemberDTO extends UserDTO {

    private static final long serialVersionUID = -6504817886555446680L;

    private String  authorityId;
    private String  authorityCode;
    private int     authorityValue;
    private boolean map;

}
