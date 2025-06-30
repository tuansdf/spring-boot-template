package com.example.sbt.common.util;

import com.example.sbt.common.util.LocaleHelper.LocaleKey;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isEmail(String input) {
        return Regex.EMAIL.matcher(input).matches();
    }

    public static String validatePassword(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() < 12 || input.length() > 64) {
            return LocaleHelper.getMessage("form.error.not_between_length", new LocaleKey("field.password"), 12, 64);
        }
        return null;
    }

    public static String validateCode(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.code"), 255);
        }
        if (Regex.CODE.matcher(input).matches()) {
            return LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.code"));
        }
        return null;
    }

    public static String validateEmail(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.email"), 255);
        }
        if (!ValidationUtils.isEmail(input)) {
            return LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.email"));
        }
        return null;
    }

    public static String validateUsername(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() < 3) {
            return LocaleHelper.getMessage("form.error.under_min_length", new LocaleKey("field.username"), 3);
        }
        if (input.length() > 64) {
            return LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.username"), 64);
        }
        if (Regex.USERNAME.matcher(input).matches()) {
            return LocaleHelper.getMessage("form.error.invalid", new LocaleKey("field.username"));
        }
        return null;
    }

    private static class Regex {
        private static final Pattern EMAIL = Pattern.compile("^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w-]{2,}$");
        private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9]+(_?[a-zA-Z0-9]+)*$");
        private static final Pattern CODE = Pattern.compile("^[a-zA-Z0-9]+(_?[a-zA-Z0-9]+)*$");
    }

}
