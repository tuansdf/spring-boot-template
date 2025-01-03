package org.tuanna.xcloneserver.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> defaultExceptionHandler(HttpServletRequest servletRequest, Exception e) throws Exception {
        log.error("default exception handler", e);
        return ExceptionUtils.toResponseEntity(e);
    }

}
