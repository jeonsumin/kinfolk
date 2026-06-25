package com.terry.backend.api.workspace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberAuthorityRequest {

    @NotNull(message = "권한을 입력해주세요.")
    private WorkspaceAuthority authority;
}
