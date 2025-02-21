package com.example.demo.modules.auth.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ForgotPasswordRequestDTO {

    private String email;

}
