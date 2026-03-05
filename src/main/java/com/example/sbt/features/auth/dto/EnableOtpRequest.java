package com.example.sbt.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnableOtpRequest {
    @NotBlank(message = "{bv.password.required}")
    private String password;
}
