package com.example.demo.module.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequestDTO {

    private String username;
    private String password;
    private String otpCode;

}
