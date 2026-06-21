package com.terry.backend.api.admin.system.menu.dto;

import com.terry.backend.core.dto.BaseSearchParam;
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
public class MenuSearchParam extends BaseSearchParam {

    private String id;
    private String parentId;
    private String code;
    private String name;
    private String use;

}
