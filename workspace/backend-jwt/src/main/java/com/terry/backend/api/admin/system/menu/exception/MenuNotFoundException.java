package com.terry.backend.api.admin.system.menu.exception;


import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class MenuNotFoundException extends SystemException {

    private static final long serialVersionUID = 6030716739278214113L;

    public MenuNotFoundException(String id) {
        super("AdminMenuService.ERROR.NotFound", new Object[] { id });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
