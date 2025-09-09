package com.example.sbt.core.helper;

import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.dto.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocaleHelper {
    private final MessageSource messageSource;

    public String getMessage(String code, Locale locale, Object... args) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        if (locale == null) {
            locale = RequestContextHolder.get().getLocale();
        }
        try {
            if (ArrayUtils.isNotEmpty(args)) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && args[i] instanceof LocaleKey(String arg) && StringUtils.isNotBlank(arg)) {
                        args[i] = getMessage(arg, locale);
                    }
                }
            }
            return messageSource.getMessage(code, args, code, locale);
        } catch (Exception e) {
            return code;
        }
    }

    public String getMessage(String code, Object... args) {
        try {
            return getMessage(code, null, args);
        } catch (Exception e) {
            return code;
        }
    }
}
