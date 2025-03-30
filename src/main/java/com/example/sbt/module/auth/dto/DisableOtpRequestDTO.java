package com.example.sbt.module.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DisableOtpRequestDTO {

    private String otpCode;
    private String password;

}
