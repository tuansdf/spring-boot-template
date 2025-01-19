package com.example.springboot.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class I18nUtils {

    private final MessageSource messageSource;

    public String getMessage(String code, Locale locale, Object[] args) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, code, locale);
    }

}
