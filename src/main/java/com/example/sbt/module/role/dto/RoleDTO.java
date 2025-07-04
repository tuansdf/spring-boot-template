package com.example.sbt.module.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private List<UUID> permissionIds;
    private List<String> permissionCodes;

    public RoleDTO(UUID id, String code, String name, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
