package com.example.sbt.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class CommonResponse<T> {

    private String message;
    private int status;
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

    public CommonResponse(String message) {
        this.data = null;
        this.status = HttpStatus.OK.value();
        this.message = message;
    }

    public CommonResponse(HttpStatus httpStatus) {
        this.data = null;
        this.status = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    public CommonResponse(String message, HttpStatus status) {
        this.data = null;
        this.status = status.value();
        this.message = message;
    }

    public CommonResponse(HttpStatus status, String message) {
        this.data = null;
        this.status = status.value();
        this.message = message;
    }

}
