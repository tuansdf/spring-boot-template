package com.example.sbt.features.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "{bv.password.required}")
    @Size(min = 12, max = 64, message = "{bv.password.size}")
    private String oldPassword;

    @NotBlank(message = "{bv.password.required}")
    @Size(min = 12, max = 64, message = "{bv.password.size}")
    private String newPassword;

    private String otpCode;
}
