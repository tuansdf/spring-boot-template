package org.tuanna.xcloneserver.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.tuanna.xcloneserver.exception.CustomException;

import java.util.List;

public class ValidationUtils {

    public static void notNull(Object input, String message) throws CustomException {
        if (input == null) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void notEmpty(String input, String message) throws CustomException {
        if (StringUtils.isEmpty(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void minLength(String input, int length, String message) throws CustomException {
        if (input != null && input.length() < length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void maxLength(String input, int length, String message) throws CustomException {
        if (input != null && input.length() > length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void betweenLength(String input, int min, int max, String message) throws CustomException {
        minLength(input, min, message);
        maxLength(input, max, message);
    }

    public static void startsWith(String input, String start, String message) throws CustomException {
        if (input != null && !input.startsWith(start)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void endsWith(String input, String end, String message) throws CustomException {
        if (input != null && !input.endsWith(end)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void isEmail(String input, String message) throws CustomException {
        if (input != null && !EmailValidator.getInstance().isValid(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static <T> void isIn(T input, List<T> valid, String message) throws CustomException {
        if (input != null && !CollectionUtils.isEmpty(valid) && !valid.contains(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

}
