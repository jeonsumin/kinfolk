package com.terry.backend.api.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class WorkspaceMemberDTO {
    private static final long serialVersionUID = 1L;

    private String memberId;
    private String name;
    private String profileImageUri;
    private WorkspaceAuthority authority;
}
