package com.example.demo.module.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendEmailRequest {

    private String toEmail;
    private String ccEmail;
    private String subject;
    private String body;

}
