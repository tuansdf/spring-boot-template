package com.example.demo.module.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChangePasswordRequestDTO {

    private String oldPassword;
    private String newPassword;

}
