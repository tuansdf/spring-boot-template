package com.example.sbt.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoRollbackException extends CustomException {
    public NoRollbackException(HttpStatus status) {
        super(status);
    }

    public NoRollbackException(String message) {
        super(message);
    }

    public NoRollbackException(String message, HttpStatus status) {
        super(message, status);
    }
}
