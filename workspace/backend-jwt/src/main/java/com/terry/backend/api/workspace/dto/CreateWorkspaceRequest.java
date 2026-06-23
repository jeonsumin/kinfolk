package com.terry.backend.api.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkspaceRequest {

    @NotBlank(message = "워크스페이스 이름을 입력해주세요.")
    @Size(max = 255, message = "워크스페이스 이름은 255자 이하로 입력해주세요.")
    private String wsNm;

    private String wsDesc;
}
