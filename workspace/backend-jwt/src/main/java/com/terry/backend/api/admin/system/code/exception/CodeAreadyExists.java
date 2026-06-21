package com.terry.backend.api.admin.system.code.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class CodeAreadyExists extends SystemException {

    private static final long serialVersionUID = 4099632792417857172L;

    public CodeAreadyExists(final String code) {
        super("AdminCodeService.ERROR.CodeAlreadyExists", new Object[] { code });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
