package com.example.sbt.module.file;

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
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

}
