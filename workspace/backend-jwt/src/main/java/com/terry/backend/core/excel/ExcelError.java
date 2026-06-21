package com.terry.backend.core.excel;

import com.terry.backend.core.messages.util.MessageSourceUtils;
import lombok.Data;

@Data
public class ExcelError {
    private int      rowNum;
    private String   message;
    private Object[] args;

    public ExcelError(int rowNum, String message) {
        this.rowNum = rowNum;
        this.message = message;
        this.args = null;
    }

    public ExcelError(int rowNum, String message, Object... args) {
        this.rowNum = rowNum;
        this.message = message;
        this.args = args;
    }

    @Override
    public String toString() {
        return String
                .format("%s : %s",
                        MessageSourceUtils.getMessage("Excel.ErrorMessageRow", rowNum),
                        MessageSourceUtils.getMessage(message, args));
    }

}
