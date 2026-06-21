package com.terry.backend.core.messages.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageSourceUtils {
    private static MessageSource messageSource;

    public static String getMessage(String code, Object... args){
        try{
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        }catch (Exception e ){
            return code;
        }
    }

    public static void setMessageSource(MessageSource messageSource) {
        MessageSourceUtils.messageSource = messageSource;
    }
}
