package com.example.sbt.module.remoteconfig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRemoteConfigRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String code;
    private String status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
