package com.terry.backend.core.excel.dto;

import com.terry.backend.core.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper=false)
public class ExcelDTO extends BaseDTO{

    private static final long serialVersionUID = 8912268526374248350L;

    private String fileId;
    private String fileName;
    private String filePath;
    private Long   fileSize;
    private String fileType;
}
