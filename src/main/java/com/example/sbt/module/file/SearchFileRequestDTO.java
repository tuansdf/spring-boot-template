package com.example.sbt.module.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchFileRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String fileType;
    private Long fileSizeFrom;
    private Long fileSizeTo;
    private Instant createdAtFrom;
    private Instant createdAtTo;
    private String orderBy;
    private String orderDirection;
    private UUID createdBy;

}
