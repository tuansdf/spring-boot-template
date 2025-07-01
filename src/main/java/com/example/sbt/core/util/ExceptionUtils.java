package com.example.sbt.core.util;

import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ExceptionUtils {

    public static <T> CommonResponse<T> toResponse(Exception e) {
        CommonResponse<T> result = new CommonResponse<>();
        if (e instanceof CustomException ce) {
            if (ce.getStatus() == null) {
                ce.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            result.setMessage(ce.getMessage());
            result.setStatus(ce.getStatus().value());
        } else {
            result.setMessage(LocaleHelper.getMessage("common.error"));
            result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

    public static <T> ResponseEntity<CommonResponse<T>> toResponseEntity(Exception e) {
        CommonResponse<T> response = toResponse(e);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
