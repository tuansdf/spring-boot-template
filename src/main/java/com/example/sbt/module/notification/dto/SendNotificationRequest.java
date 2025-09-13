package com.example.sbt.module.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendNotificationRequest {
    private String title;
    private String body;
    private String data;
    private String topic;
    private Set<String> tokens;
}
