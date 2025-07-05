package com.example.sbt.core.helper;

import com.example.sbt.core.dto.LocaleKey;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ValidationHelper {
    private final LocaleHelper localeHelper;

    public String validatePassword(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() < 12 || input.length() > 64) {
            return localeHelper.getMessage("validation.error.not_between_length", new LocaleKey("field.password"), 12, 64);
        }
        return null;
    }

    public String validateCode(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.code"), 255);
        }
        if (Regex.CODE.matcher(input).matches()) {
            return localeHelper.getMessage("validation.error.invalid", new LocaleKey("field.code"));
        }
        return null;
    }

    public String validateEmail(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.email"), 255);
        }
        if (!Regex.EMAIL.matcher(input).matches()) {
            return localeHelper.getMessage("validation.error.invalid", new LocaleKey("field.email"));
        }
        return null;
    }

    public String validateUsername(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() < 3) {
            return localeHelper.getMessage("validation.error.under_min_length", new LocaleKey("field.username"), 3);
        }
        if (input.length() > 64) {
            return localeHelper.getMessage("validation.error.over_max_length", new LocaleKey("field.username"), 64);
        }
        if (!Regex.USERNAME.matcher(input).matches()) {
            return localeHelper.getMessage("validation.error.invalid", new LocaleKey("field.username"));
        }
        return null;
    }

    private static class Regex {
        private static final Pattern EMAIL = Pattern.compile("^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w-]{2,}$");
        private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9]+(_?[a-zA-Z0-9]+)*$");
        private static final Pattern CODE = Pattern.compile("^[a-zA-Z0-9]+(_?[a-zA-Z0-9]+)*$");
    }
}
