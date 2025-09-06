package com.example.sbt.module.email.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {
    private UUID id;
    private UUID userId;
    private String toEmail;
    private String ccEmail;
    private String subject;
    private String body;
    @JsonIgnore
    private Integer retryCount;
    @JsonIgnore
    private String type;
    private String status;
    @JsonIgnore
    private String sendStatus;
    private Boolean isHtml;
    private Instant createdAt;
    private Instant updatedAt;
}
