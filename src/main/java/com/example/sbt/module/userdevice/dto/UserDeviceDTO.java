package com.example.sbt.module.userdevice.dto;

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
public class UserDeviceDTO {

    private UUID id;
    private UUID userId;
    private String fcmToken;
    private Instant createdAt;
    private Instant updatedAt;

}
