package com.example.sbt.module.configuration.dto;

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
public class ConfigurationDTO {

    private UUID id;
    private String code;
    private String value;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

}
