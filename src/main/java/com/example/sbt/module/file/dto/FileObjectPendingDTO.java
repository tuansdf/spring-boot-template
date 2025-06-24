package com.example.sbt.module.file.dto;

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
public class FileObjectPendingDTO {

    private UUID id;
    private String filePath;
    private String fileUploadUrl;
    private String fileName;
    private String fileType;
    private UUID createdBy;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

}
