package com.example.demo.modules.auth.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginRequestDTO {

    private String username;
    private String password;
    private String otp;

}
