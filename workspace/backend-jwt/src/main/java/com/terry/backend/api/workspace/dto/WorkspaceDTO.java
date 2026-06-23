package com.terry.backend.api.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class WorkspaceDTO {
    private static final long serialVersionUID = 1L;

    private String id;
    private String wsNm;
    private String wsDesc;
    private String wsOwnerId;
    private WorkspaceAuthority authority;
    private Date registDt;
}
