package com.example.springboot.modules.user.dtos;

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
