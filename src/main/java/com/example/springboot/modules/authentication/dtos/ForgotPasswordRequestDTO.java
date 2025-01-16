package com.example.springboot.modules.authentication.dtos;

import com.example.springboot.exception.CustomException;
import com.example.springboot.utils.ValidationUtils;
import lombok.*;

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
