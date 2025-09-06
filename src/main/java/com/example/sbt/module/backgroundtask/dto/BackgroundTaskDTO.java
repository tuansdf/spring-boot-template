package com.example.sbt.module.backgroundtask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundTaskDTO {
    private UUID id;
    private UUID fileId;
    private String cacheKey;
    private String type;
    private String status;
    private String errorMessage;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
