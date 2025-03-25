package com.example.sbt.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@ToString
@Builder
public class CommonResponse<T> {

    private String message;
    private int status;
    private T data;

    @JsonIgnore
    private HttpStatus httpStatus;

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
