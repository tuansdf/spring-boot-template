package com.example.sbt.module.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Set<String> roles;
    private Set<String> permissions;

}
