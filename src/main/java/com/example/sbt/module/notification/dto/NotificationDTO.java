package com.example.sbt.module.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String body;
    private String data;
    private String topic;
    @JsonIgnore
    private Integer retryCount;
    private String type;
    private String status;
    @JsonIgnore
    private String sendStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
