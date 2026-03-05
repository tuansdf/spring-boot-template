package com.example.sbt.infrastructure.exception;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ValidationException extends CustomException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(HttpStatus.BAD_REQUEST);
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        if (CollectionUtils.isEmpty(errors)) {
            return HttpStatus.BAD_REQUEST.getReasonPhrase();
        }
        return String.join(", ", errors);
    }
}
