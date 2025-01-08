package org.tuanna.xcloneserver.modules.auth.dtos;

import lombok.*;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.utils.ValidationUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RegisterRequestDTO {

    private String username;
    private String email;
    private String password;
    private String name;

    public void validate() throws CustomException {
        ValidationUtils.notEmpty(this.username, "Username is required");
        ValidationUtils.maxLength(this.username, 255, "Username exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.email, "Email is required");
        ValidationUtils.maxLength(this.email, 255, "Email exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.password, "Password is required");
        ValidationUtils.betweenLength(this.password, 12, 255, "Password must be between 12 and 255 characters");
    }

}
