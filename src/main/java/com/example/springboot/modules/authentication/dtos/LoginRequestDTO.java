package com.example.springboot.modules.authentication.dtos;

import com.example.springboot.exception.CustomException;
import com.example.springboot.utils.ValidationUtils;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequestDTO {

    private String username;
    private String password;

    public void validate() throws CustomException {
        ValidationUtils.notEmpty(this.username, "Username is required");
        ValidationUtils.maxLength(this.username, 255, "Username exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.password, "Password is required");
        ValidationUtils.betweenLength(this.password, 12, 255, "Password must be between 12 and 255 characters");
    }

}
