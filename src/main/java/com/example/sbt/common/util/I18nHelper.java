package com.example.sbt.common.util;

import com.example.sbt.common.dto.RequestContextHolder;
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
        try {
            if (ArrayUtils.isNotEmpty(args)) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && args[i] instanceof String v && v.startsWith("#")) {
                        args[i] = getMessage(v.substring(1), locale);
                    }
                }
            }
            return messageSource.getMessage(code, args, code, locale);
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code, Object... args) {
        try {
            return getMessage(code, RequestContextHolder.get().getLocale(), args);
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code, Locale locale) {
        try {
            return messageSource.getMessage(code, null, code, locale);
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code) {
        try {
            return getMessage(code, RequestContextHolder.get().getLocale());
        } catch (Exception e) {
            return code;
        }
    }

}
