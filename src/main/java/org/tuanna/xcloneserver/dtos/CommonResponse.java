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

    private String message;
    private int status;
    private Object data;

    @JsonIgnore
    private HttpStatus httpStatus;

    public CommonResponse(Object data) {
        this.data = data;
        this.setStatus(200);
        this.setMessage("OK");
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
