package com.example.sbt.common.util;

import com.example.sbt.common.dto.RequestHolder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Scope("singleton")
public class LocaleHelper {

    private static MessageSource messageSource;

    public LocaleHelper(MessageSource messageSource) {
        LocaleHelper.messageSource = messageSource;
    }

    public static String getMessage(String code, Locale locale, Object... args) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if (locale == null) {
            locale = RequestHolder.getContext().getLocale();
        }
        try {
            if (ArrayUtils.isNotEmpty(args)) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && args[i] instanceof LocaleKey v && v.arg != null) {
                        args[i] = getMessage(v.arg, locale);
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
            return getMessage(code, RequestHolder.getContext().getLocale(), args);
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code, Locale locale) {
        try {
            return getMessage(code, locale, (Object) null);
        } catch (Exception e) {
            return code;
        }
    }

    public static String getMessage(String code) {
        try {
            return getMessage(code, RequestHolder.getContext().getLocale());
        } catch (Exception e) {
            return code;
        }
    }

    public record LocaleKey(String arg) {
    }

}
