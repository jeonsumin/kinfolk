package com.terry.backend.web.file.exception;

import com.terry.backend.core.excption.SystemException;

public class FileNotFoundException extends SystemException {

    private static final long serialVersionUID = -6228436192711716426L;

    public FileNotFoundException() {
        super("FileNotFoundException");
    }

}
