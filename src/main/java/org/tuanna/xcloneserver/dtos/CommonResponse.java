package org.tuanna.xcloneserver.dtos;

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

    @Builder.Default
    private String message = HttpStatus.OK.getReasonPhrase();
    @Builder.Default
    private int status = HttpStatus.OK.value();
    private Object data;

    @JsonIgnore
    private HttpStatus httpStatus;

    public CommonResponse(Object data) {
        this.data = data;
    }

    public CommonResponse(String message, int status) {
        this.data = null;
        this.status = status;
        this.message = message;
    }

    public CommonResponse(HttpStatus httpStatus) {
        this.data = null;
        this.status = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

}
