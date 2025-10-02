package com.example.sbt.features.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {
    private String toEmail;
    private String ccEmail;
    private String subject;
    private String body;
    private Boolean isHtml;
}
