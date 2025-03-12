package com.example.demo.module.auth.dto;

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

}
