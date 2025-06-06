package com.example.sbt.module.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest {

    private String toEmail;
    private String ccEmail;
    private String subject;
    private String body;
    private Boolean isHtml;

}
