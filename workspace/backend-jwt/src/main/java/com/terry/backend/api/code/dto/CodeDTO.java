package com.terry.backend.api.code.dto;

import com.terry.backend.api.admin.system.code.CodeType;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class CodeDTO extends BaseDTO {

    private static final long serialVersionUID = 4014764178438399348L;

    private String                  id;
    private String                  parentId;
    @NotEmpty
    @Size(min = 1, max = 40)
    private String                  code;
    @NotEmpty
    @Size(min = 1, max = 200)
    private String                  name;
    private String                  description;
    @NotNull
    @Min(0)
    private Integer                 sort;
    private String                  path;
    private Integer                 level;
    @NotNull
    private CodeType type;
    private String                  value1;
    private String                  value2;
    private String                  value3;
    @NotEmpty

    private String                  use;
}
