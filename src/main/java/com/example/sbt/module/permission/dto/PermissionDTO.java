package com.example.sbt.module.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private UUID id;
    private String code;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
