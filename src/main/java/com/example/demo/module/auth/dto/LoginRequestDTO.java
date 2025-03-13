package com.example.demo.module.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequestDTO {

    private String username;
    private String password;
    private String otpCode;

}
