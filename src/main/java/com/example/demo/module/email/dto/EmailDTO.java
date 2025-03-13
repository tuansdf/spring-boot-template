package com.example.demo.module.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDTO {

    private UUID id;
    private UUID userId;
    private String fromEmail;
    private String toEmail;
    private String ccEmail;
    private String subject;
    private String content;
    private Integer type;
    private Integer retryCount;
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

}
