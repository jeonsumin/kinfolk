package com.terry.backend.core.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseSearchParam {
    private String      id;
    private String      userId;
    private String      projectId;

    private String        startDt;
    private String        endDt;

    private String      historyId;

}
