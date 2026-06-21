package com.terry.backend.api.admin.system.authority.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class AuthorityNotFoundException extends SystemException {

    private static final long serialVersionUID = 5541663725293808414L;

    public AuthorityNotFoundException(final String id) {
        super("AdminAuthorityService.Error.NotFound", new Object[] { id });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
