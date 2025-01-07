package org.tuanna.xcloneserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.tuanna.xcloneserver.exception.CustomException;

public class ValidationUtils {

    public static void require(String input, String message) throws CustomException {
        if (StringUtils.isEmpty(input)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void minLength(String input, int length, String message) throws CustomException {
        require(input, message);
        if (input.length() < length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void maxLength(String input, int length, String message) throws CustomException {
        require(input, message);
        if (input.length() > length) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void startsWith(String input, String start, String message) throws CustomException {
        require(input, message);
        if (!input.startsWith(start)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void endsWith(String input, String end, String message) throws CustomException {
        require(input, message);
        if (!input.endsWith(end)) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

    public static void require(Object input, String message) throws CustomException {
        if (input == null) {
            throw new CustomException(message, HttpStatus.BAD_REQUEST);
        }
    }

}
