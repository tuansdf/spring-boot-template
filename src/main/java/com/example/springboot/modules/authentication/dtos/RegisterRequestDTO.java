package com.example.springboot.modules.authentication.dtos;

import com.example.springboot.exception.CustomException;
import com.example.springboot.utils.ValidationUtils;
import lombok.*;

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
