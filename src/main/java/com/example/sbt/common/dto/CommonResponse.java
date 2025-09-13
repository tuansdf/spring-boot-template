package com.example.sbt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CommonResponse<T> {
    private String message;
    private int status = HttpStatus.OK.value();
    private T data;

    public CommonResponse() {
        this.data = null;
        this.status = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
    }

    public CommonResponse(T data) {
        this.data = data;
        this.status = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
    }

    public CommonResponse(String message, HttpStatus status) {
        this.data = null;
        this.status = status.value();
        this.message = message;
    }
}
