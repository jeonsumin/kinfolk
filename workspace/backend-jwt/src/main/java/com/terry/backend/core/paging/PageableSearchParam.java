package com.terry.backend.core.paging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.terry.backend.core.dto.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PageableSearchParam extends BaseSearchParam {

    private Integer page;
    private Integer size;
    @JsonIgnore
    @Builder.Default
    private String  orderBy = "ORDER BY UPDT_DT DESC";

}
