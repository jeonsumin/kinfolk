package com.terry.backend.api.admin.system.menu.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class MenuCodeAlreadyExists extends SystemException {

  private static final long serialVersionUID = -8198537199652985217L;

  public MenuCodeAlreadyExists() {
    super("AdminMenuService.ERROR.CodeAlreadyExists");
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.CONFLICT;
  }

}
