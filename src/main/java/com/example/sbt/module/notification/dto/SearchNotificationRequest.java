package com.example.sbt.module.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchNotificationRequest {
    private Long pageNumber;
    private Long pageSize;
    private UUID userId;
    private String status;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
