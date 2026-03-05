package com.example.sbt.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestActivateAccountRequest {
    @NotBlank(message = "{bv.email.required}")
    @Email(message = "{bv.email.invalid}")
    @Size(max = 255, message = "{bv.email.size}")
    private String email;
}
