package com.example.demo.common.util;

import com.example.demo.common.constant.CommonRegex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;

public class ValidationUtils {

    public static boolean isEmail(String input) {
        return !EmailValidator.getInstance().isValid(input);
    }

    public static boolean isPattern(String input, String pattern) {
        return new RegexValidator(pattern).isValid(input);
    }

    public static String validatePassword(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() < 12 || input.length() > 64) {
            return I18nHelper.getMessageX("form.error.not_between_length", "field.password", 12, 64);
        }
        return null;
    }

    public static String validateCode(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return I18nHelper.getMessageX("form.error.over_max_length", "field.code", 255);
        }
        if (!ValidationUtils.isPattern(input, CommonRegex.CODE)) {
            return I18nHelper.getMessageX("form.error.invalid", "field.code");
        }
        return null;
    }

    public static String validateEmail(String input) {
        if (StringUtils.isEmpty(input)) return null;
        if (input.length() > 255) {
            return I18nHelper.getMessageX("form.error.over_max_length", "field.email", 255);
        }
        if (!ValidationUtils.isEmail(input)) {
            return I18nHelper.getMessageX("form.error.invalid", "field.email");
        }
        return null;
    }

}
