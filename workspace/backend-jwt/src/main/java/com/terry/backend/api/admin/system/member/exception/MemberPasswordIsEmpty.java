package com.terry.backend.api.admin.system.member.exception;

import com.terry.backend.core.excption.SystemException;

public class MemberPasswordIsEmpty extends SystemException {

  private static final long serialVersionUID = -5548476485806182258L;

  public MemberPasswordIsEmpty() {
    super("AdminMemberService.ERROR.PasswordIsEmpty");
  }

}
