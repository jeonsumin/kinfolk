package com.terry.backend.core.excption;

import com.terry.backend.core.messages.util.MessageSourceUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

public class SystemException extends Exception {
    private static final long serialVersionUID = -5374922291531572653L;

    protected final String messageCode;
    protected final Object[] args;
    public HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    @Getter
    @Setter
    protected List<SystemException> exceptions;

    public SystemException(final String messageCode, final Object... args) {
        this.messageCode = messageCode;
        this.args = args;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public SystemException(HttpStatus httpStatus, final String messageCode, final Object... args) {
        this.messageCode = messageCode;
        this.args = args;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {return messageCode; }

    @Override
    public String getMessage() {
        return MessageSourceUtils.getMessage(messageCode, args);
    }
}
