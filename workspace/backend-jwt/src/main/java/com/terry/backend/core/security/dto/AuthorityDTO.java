package com.terry.backend.core.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class AuthorityDTO implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = -36948021856385422L;

    private String authorityId;
    private String authorityCode;
    private String authority;

}
