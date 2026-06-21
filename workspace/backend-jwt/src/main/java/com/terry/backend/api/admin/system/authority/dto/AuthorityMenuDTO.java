package com.terry.backend.api.admin.system.authority.dto;

import com.terry.backend.api.admin.system.menu.MenuType;
import com.terry.backend.core.dto.BaseDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AuthorityMenuDTO extends BaseDTO {

    private static final long serialVersionUID = 3774917248394516684L;

    private String                 authorityId;
    private String                 authorityCode;
    private int                    authorityValue;
    private boolean                map;
    private String                 id;
    private String                 parentId;
    @NotEmpty
    @Size(min = 4, max = 40)
    private String                 code;
    @NotEmpty
    @Size(min = 1, max = 200)
    private String                 name;
    private String                 description;
    @NotNull
    @Min(0)
    private Integer                sort;
    @NotNull
    @Min(0)
    private Integer                level;
    private String                 icon;
    private String                 uri;
    @NotNull
    private MenuType type;
    @NotEmpty
    @Size(min = 1, max = 1)
    private String                 use;
    private List<AuthorityMenuDTO> children;

    // 하나 더 추가
    private int levelAuthor;

}
