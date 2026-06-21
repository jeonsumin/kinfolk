package com.terry.backend.api.admin.system.member.exception;

import com.terry.backend.core.excption.SystemException;
import org.springframework.http.HttpStatus;

public class MemberNotFound extends SystemException {

    private static final long serialVersionUID = 5018511036363498124L;

    public MemberNotFound(final String id) {
        super("AdminMemberService.ERROR.NotFound", new Object[] { id });
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
