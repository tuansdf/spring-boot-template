package com.example.demo.util;

import com.example.demo.config.RequestContextHolder;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Scope("singleton")
public class I18nHelper {

    private static MessageSource messageSource;

    public I18nHelper(MessageSource messageSource) {
        I18nHelper.messageSource = messageSource;
    }

    public static String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public static String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, code, locale);
    }

    public static String getMessage(String code) {
        return messageSource.getMessage(code, null, code, RequestContextHolder.get().getLocale());
    }

    public static String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code, RequestContextHolder.get().getLocale());
    }

    public static String getMessageX(String code, Locale locale, Object... args) {
        if (ArrayUtils.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null && args[i] instanceof String v) {
                    args[i] = getMessage(v, locale);
                }
            }
        }
        return messageSource.getMessage(code, args, code, locale);
    }

    public static String getMessageX(String code, Object... args) {
        return getMessageX(code, RequestContextHolder.get().getLocale(), args);
    }

}
