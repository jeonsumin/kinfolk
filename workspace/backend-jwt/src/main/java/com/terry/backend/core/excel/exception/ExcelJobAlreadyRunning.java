package com.terry.backend.core.excel.exception;

import com.terry.backend.core.excption.SystemException;

public class ExcelJobAlreadyRunning extends SystemException {

    private static final long serialVersionUID = -8052568766755389772L;

    public ExcelJobAlreadyRunning() {
        super("excel.ERROR.JobAlreadyRunning");
    }
}
