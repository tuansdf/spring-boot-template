package com.example.sbt.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "{bv.token.required}")
    private String token;

    @NotBlank(message = "{bv.password.required}")
    @Size(min = 12, max = 64, message = "{bv.password.size}")
    private String newPassword;
}
