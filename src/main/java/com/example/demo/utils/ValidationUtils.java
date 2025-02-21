package com.example.demo.utils;

import com.example.demo.exception.CustomException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationUtils {

    public static void notNull(Object input, String message) {
        if (input == null) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void notEmpty(String input, String message) {
        if (StringUtils.isEmpty(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void minLength(String input, int length, String message) {
        if (input != null && input.length() < length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void maxLength(String input, int length, String message) {
        if (input != null && input.length() > length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void betweenLength(String input, int min, int max, String message) {
        minLength(input, min, message);
        maxLength(input, max, message);
    }

    public static void startsWith(String input, String start, String message) {
        if (input != null && !input.startsWith(start)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void endsWith(String input, String end, String message) {
        if (input != null && !input.endsWith(end)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void isEmail(String input, String message) {
        if (input != null && !EmailValidator.getInstance().isValid(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void isPattern(String input, String pattern, String message) {
        if (input != null && !(new RegexValidator(pattern).isValid(input))) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static <T> void isIn(T input, List<T> valid, String message) {
        if (input != null && CollectionUtils.isNotEmpty(valid) && !valid.contains(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

}
