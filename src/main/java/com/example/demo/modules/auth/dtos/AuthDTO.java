package com.example.demo.modules.auth.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDTO {

    private String accessToken;
    private String refreshToken;
    private String token;
    private String secret;
    private String otp;
    private String password;
    private UUID userId;
    private String username;
    private String email;
    private String name;
    private Set<String> permissions;

}
