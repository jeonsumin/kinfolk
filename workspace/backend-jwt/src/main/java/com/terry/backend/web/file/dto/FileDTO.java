package com.terry.backend.web.file.dto;

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
public class FileDTO extends BaseDTO {

    private static final long serialVersionUID = 8215061954996653578L;

    private String fileId;
    private String fileName;
    private String filePath;
    private Long   fileSize;
    private String fileType;

}
