package com.example.sbt.module.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("configuration")
public class ConfigurationDTO {

    private UUID id;
    @Id
    private String code;
    private String value;
    private String description;
    private String status;
    private Boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;

}
