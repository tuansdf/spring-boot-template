package com.example.sbt.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisableOtpRequest {
    private String otpCode;
    private String password;
}
