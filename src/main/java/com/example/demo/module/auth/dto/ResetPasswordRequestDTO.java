package com.example.demo.module.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ResetPasswordRequestDTO {

    private String token;
    private String newPassword;

}
