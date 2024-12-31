package org.tuanna.xcloneserver.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomException extends Exception {

    private HttpStatus status;

    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
