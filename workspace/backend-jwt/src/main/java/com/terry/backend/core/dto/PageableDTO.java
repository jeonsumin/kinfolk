package com.terry.backend.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(value = Include.NON_NULL)
public class PageableDTO extends BaseDTO {

    private static final long serialVersionUID = -8801251662615163813L;

    private Integer rowNum;
    @JsonIgnore
    private Integer totalItems;

}
