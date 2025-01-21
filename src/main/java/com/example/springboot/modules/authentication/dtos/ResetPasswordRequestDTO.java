package com.example.springboot.modules.authentication.dtos;

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
