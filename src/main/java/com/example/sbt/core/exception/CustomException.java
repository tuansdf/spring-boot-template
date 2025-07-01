package com.example.sbt.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {

    protected HttpStatus status;

    private CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public CustomException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
