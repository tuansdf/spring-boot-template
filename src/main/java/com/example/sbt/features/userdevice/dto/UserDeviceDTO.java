package com.example.sbt.features.userdevice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceDTO {
    private UUID id;
    private UUID userId;
    private String fcmToken;
    private Instant createdAt;
    private Instant updatedAt;
}
