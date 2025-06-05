package com.example.sbt.module.userdevice.dto;

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
public class UserDeviceDTO {

    private UUID id;
    private UUID userId;
    private String fcmToken;
    private Instant createdAt;
    private Instant updatedAt;

}
