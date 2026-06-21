package com.terry.backend.core.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ExcelHeader {


    private static final long serialVersionUID = -3943012090886015372L;

    private final boolean key;
    private final String  label;
    private final String  field;
    private int           columnIndex;

    public ExcelHeader(ExcelHeader o, final int columnIndex) {
        this.key = o.isKey();
        this.label = o.getLabel();
        this.field = o.getField();
        this.columnIndex = columnIndex;
    }

    public ExcelHeader(ExcelHeader o, final String label, final int columnIndex) {
        this.key = o.isKey();
        this.label = label;
        this.field = o.getField();
        this.columnIndex = columnIndex;
    }

}
