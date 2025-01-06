package org.tuanna.xcloneserver.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class CustomException extends Exception {

    private HttpStatus status;

    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(HttpStatus status) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
