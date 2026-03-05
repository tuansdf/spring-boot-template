package com.example.sbt.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "{bv.username.required}")
    @Size(min = 3, max = 64, message = "{bv.username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]+(_?[a-zA-Z0-9]+)*$", message = "{bv.username.pattern}")
    private String username;

    @NotBlank(message = "{bv.password.required}")
    @Size(min = 12, max = 64, message = "{bv.password.size}")
    private String password;

    private String otpCode;
}
