package com.example.sbt.module.email.dto;

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
    private String toEmail;
    private String ccEmail;
    private String subject;
    private String body;
    private Integer retryCount;
    private Integer type;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isHtml;

}
