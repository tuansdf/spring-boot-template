package com.example.sbt.module.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchConfigurationRequest {
    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Boolean isEnabled;
    private Boolean isPublic;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
