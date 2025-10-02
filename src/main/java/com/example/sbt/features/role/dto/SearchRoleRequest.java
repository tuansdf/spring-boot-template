package com.example.sbt.features.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRoleRequest {
    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
