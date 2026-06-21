package com.terry.backend.api.admin.system.member.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class MemberLoginIdAlreadyExists extends SystemException {

    private static final long serialVersionUID = 6608702098687777506L;

    public MemberLoginIdAlreadyExists(final String loginId) {
        super("AdminMemberService.ERROR.LoginIdAlreadyExists", new Object[] { loginId });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
