package com.example.demo.common.exception;

import com.example.demo.common.dto.CommonResponse;
import com.example.demo.common.util.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> authorizationDeniedHandler(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Exception e) throws Exception {
        return new ResponseEntity<>(new CommonResponse<>(HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> defaultExceptionHandler(HttpServletRequest servletRequest, Exception e) throws Exception {
        log.error("defaultExceptionHandler", e);
        return ExceptionUtils.toResponseEntity(e);
    }

}
