package com.example.springboot.modules.authentication.dtos;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthDTO {

    private String accessToken;
    private String refreshToken;
    private String token;
    private UUID userId;
    private String username;
    private String email;
    private String name;
    private Set<String> permissions;

}
