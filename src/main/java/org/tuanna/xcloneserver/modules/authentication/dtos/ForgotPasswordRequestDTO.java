package org.tuanna.xcloneserver.modules.authentication.dtos;

import lombok.*;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.utils.ValidationUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ForgotPasswordRequestDTO {

    private String email;

    public void validate() throws CustomException {
        ValidationUtils.notEmpty(this.email, "Email is required");
        ValidationUtils.isEmail(this.email, "Email is invalid");
        ValidationUtils.maxLength(this.email, 255, "Email exceeds the maximum length of 255 characters");
    }

}
