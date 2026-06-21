package com.terry.backend.core.paging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.terry.backend.core.dto.PageableDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PageableResult<T extends PageableDTO> {

    private Integer page;
    private Integer size;
    private Integer totalItems;
    private List<T> contents;

    private PageableResult() {
        super();
    }

    public PageableResult(List<T> contents, PageableSearchParam param) {
        this();

        this.page = param.getPage();
        this.size = param.getSize();

        if (contents != null && !contents.isEmpty()) {
            this.totalItems = ((PageableDTO) contents.get(0)).getTotalItems();
            this.contents = contents;
        } else {
            this.totalItems = 0;
            this.contents = new ArrayList<>();
        }
    }

    public Integer totalPages() {
        return (int) Math.ceil((double) totalItems / (double) size);
    }

}
