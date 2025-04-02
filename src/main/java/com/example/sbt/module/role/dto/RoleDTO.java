package com.example.sbt.module.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<UUID> permissionIds;
    private Set<String> permissionCodes;

    public RoleDTO(UUID id, String code, String name, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
