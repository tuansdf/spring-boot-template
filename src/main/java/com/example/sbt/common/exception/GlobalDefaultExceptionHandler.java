package com.example.sbt.common.exception;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.util.ExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    // Missing request body exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingRequestBody() {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Request body is missing or invalid");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Missing request param exception
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Missing required parameter: " + ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Request param parsing exception
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Invalid value for parameter: " + ex.getName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleNotFound(HttpServletRequest servletRequest) {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage("Endpoint not found: " + servletRequest.getServletPath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> authorizationDeniedHandler() {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setMessage(HttpStatus.FORBIDDEN.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingRequestBody(CustomException ex) {
        return ExceptionUtils.toResponseEntity(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> defaultExceptionHandler(Exception e) {
        log.error("defaultExceptionHandler ", e);
        return ExceptionUtils.toResponseEntity(e);
    }

}
