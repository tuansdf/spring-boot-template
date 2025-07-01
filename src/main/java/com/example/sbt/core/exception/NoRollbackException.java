package com.example.sbt.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
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
