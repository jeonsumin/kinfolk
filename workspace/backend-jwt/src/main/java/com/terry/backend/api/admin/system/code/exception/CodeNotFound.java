package com.terry.backend.api.admin.system.code.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class CodeNotFound extends SystemException {

    private static final long serialVersionUID = 3214665326878597271L;

    public CodeNotFound(String id) {
        super("AdminCodeService.ERROR.NotFound", new Object[] { id });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
