package com.terry.backend.web.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberMenuAuthorityDTO implements Serializable {

    private static final long serialVersionUID = -4449908901439380459L;

    private String  menuId;
    private String  menuCode;
    private String  authoriyId;
    private String  authoriyCode;
    private Integer authorityValue;

}
