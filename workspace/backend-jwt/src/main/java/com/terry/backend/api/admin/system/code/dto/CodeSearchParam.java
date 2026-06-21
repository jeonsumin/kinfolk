package com.terry.backend.api.admin.system.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CodeSearchParam {

    private String id;
    private String parentId;
    private String code;
    private String name;
    private String path;
    @JsonIgnore
    private String matchCode;

}
