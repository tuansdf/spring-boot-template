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
    @Builder.Default
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
