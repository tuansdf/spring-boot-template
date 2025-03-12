package com.example.demo.module.permission.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PermissionDTO {

    private UUID id;
    private String code;
    private String name;
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

}
