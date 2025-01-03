package org.tuanna.xcloneserver.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CommonResponse {

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
