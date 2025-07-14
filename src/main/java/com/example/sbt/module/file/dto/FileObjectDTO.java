package com.example.sbt.module.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class FileObjectDTO {
    private UUID id;
    @JsonIgnore
    private String filePath;
    @JsonIgnore
    private String previewFilePath;
    private String filename;
    private String fileType;
    private Long fileSize;
    private String cacheKey;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    private String fileUrl;
    private String previewFileUrl;

    public FileObjectDTO(UUID id, String filePath, String previewFilePath, String filename, String fileType, Long fileSize, String cacheKey, UUID createdBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.filePath = filePath;
        this.previewFilePath = previewFilePath;
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.cacheKey = cacheKey;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
