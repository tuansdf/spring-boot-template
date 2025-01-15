package org.tuanna.xcloneserver.utils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nUtils {

    private static MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }

    public static String getMessage(String code, Object[] args, Locale locale) {
        if (messageSource == null) return "";
        return messageSource.getMessage(code, args, locale);
    }

}
