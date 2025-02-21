package com.example.demo.modules.auth.dtos;

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
