package com.example.demo.modules.user.dtos;

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
