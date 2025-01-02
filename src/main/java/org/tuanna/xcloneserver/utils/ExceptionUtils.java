package org.tuanna.xcloneserver.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.exceptions.CustomException;

public class ExceptionUtils {

    public static CommonResponse toResponse(Exception e) {
        CommonResponse result = CommonResponse.builder()
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        if (e instanceof CustomException ce) {
            result.setMessage(ce.getMessage());
            result.setStatus(ce.getStatus().value());
            result.setHttpStatus(ce.getStatus());
        }
        return result;
    }

    public static ResponseEntity<CommonResponse> toResponseEntity(Exception e) {
        CommonResponse response = toResponse(e);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
