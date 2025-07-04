package com.example.sbt.module.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRoleRequestDTO {
    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
