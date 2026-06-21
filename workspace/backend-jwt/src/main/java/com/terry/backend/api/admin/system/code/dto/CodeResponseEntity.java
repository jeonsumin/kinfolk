package com.terry.backend.api.admin.system.code.dto;


import com.terry.backend.api.code.dto.CodeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeResponseEntity {

    private String        path;
    private List<CodeDTO> contents;

}
