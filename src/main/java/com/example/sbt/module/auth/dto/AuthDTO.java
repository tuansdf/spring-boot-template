package com.example.sbt.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDTO {
    private String accessToken;
    private String refreshToken;
    private String token;
    private String otpSecret;
    private String otpCode;
    private String password;
    private UUID userId;
    private String username;
    private String email;
    private String name;
    private List<String> roles;
    private List<String> permissions;
}
