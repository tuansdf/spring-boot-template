package com.example.demo.module.notification.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NotificationDTO {

    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private String data;
    private String topic;
    private Integer retryCount;
    private Integer type;
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

}
