package org.tuanna.xcloneserver.modules.authentication.dtos;

import lombok.*;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.utils.ValidationUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ResetPasswordRequestDTO {

    private String token;
    private String newPassword;

    public void validate() throws CustomException {
        ValidationUtils.notEmpty(this.token, "Token is required");
        ValidationUtils.notEmpty(this.newPassword, "Password is required");
        ValidationUtils.betweenLength(this.newPassword, 12, 255, "Password must be between 12 and 255 characters");
    }

}
