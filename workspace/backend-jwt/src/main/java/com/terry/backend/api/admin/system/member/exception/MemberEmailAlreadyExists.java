package com.terry.backend.api.admin.system.member.exception;


import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class MemberEmailAlreadyExists extends SystemException {

    private static final long serialVersionUID = -6282126152786091302L;

    public MemberEmailAlreadyExists(final String email) {
        super("AdminMemberService.ERROR.EmailAlreadyExists", new Object[] { email });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

}
