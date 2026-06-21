package com.terry.backend.web.security.dto;

import com.terry.backend.core.dto.BaseDTO;
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
public class UserLogDTO extends BaseDTO {

    private static final long serialVersionUID = -4446709797785372486L;

    private String id;
    private String accessUri;
    private String accessMethod;

}
