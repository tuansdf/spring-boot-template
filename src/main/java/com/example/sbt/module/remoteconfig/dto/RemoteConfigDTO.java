package com.example.sbt.module.remoteconfig.dto;

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
public class RemoteConfigDTO {

    private UUID id;
    private String code;
    private String value;
    private String description;
    private String status;
    private Boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;

}
