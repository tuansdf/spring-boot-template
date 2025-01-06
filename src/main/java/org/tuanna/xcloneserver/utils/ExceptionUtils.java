package org.tuanna.xcloneserver.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.exception.CustomException;

public class ExceptionUtils {

    public static <T> CommonResponse<T> toResponse(Exception e) {
        CommonResponse<T> result = CommonResponse.<T>builder()
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();
        if (e instanceof CustomException ce) {
            result.setMessage(ce.getMessage());
            result.setStatus(ce.getStatus().value());
            result.setHttpStatus(ce.getStatus());
        }
        return result;
    }

    public static <T> ResponseEntity<CommonResponse<T>> toResponseEntity(Exception e) {
        CommonResponse<T> response = toResponse(e);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
