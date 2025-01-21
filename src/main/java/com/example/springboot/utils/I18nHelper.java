package com.example.springboot.utils;

import com.example.springboot.configs.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class I18nHelper {

    private final MessageSource messageSource;

    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, code, locale);
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, code, RequestContextHolder.get().getLocale());
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code, RequestContextHolder.get().getLocale());
    }

    public String getMessageX(String code, Locale locale, Object... args) {
        if (ArrayUtils.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null && args[i] instanceof String v) {
                    args[i] = getMessage(v);
                }
            }
        }
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getMessageX(String code, Object... args) {
        return getMessageX(code, RequestContextHolder.get().getLocale(), args);
    }

}
