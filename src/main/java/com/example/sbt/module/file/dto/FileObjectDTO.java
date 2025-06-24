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
    private String fileUrl;
    private String previewFileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    public FileObjectDTO(UUID id, String filePath, String previewFilePath, String fileName, String fileType, Long fileSize, UUID createdBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.filePath = filePath;
        this.previewFilePath = previewFilePath;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
