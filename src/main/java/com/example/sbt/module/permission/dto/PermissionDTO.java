package com.example.sbt.module.permission.dto;

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
public class PermissionDTO {

    private UUID id;
    private String code;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

}
