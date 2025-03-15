package com.example.demo.module.role.dto;

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
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<UUID> permissionIds;

}
