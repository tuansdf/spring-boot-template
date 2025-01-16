package com.example.springboot.modules.authentication.dtos;

import com.example.springboot.exception.CustomException;
import com.example.springboot.utils.ValidationUtils;
import lombok.*;

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
