package com.example.sbt.common.util;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ExceptionUtils {

    public static <T> CommonResponse<T> toResponse(Exception e) {
        CommonResponse<T> result = CommonResponse.<T>builder()
                .message(I18nHelper.getMessage("common.error"))
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
