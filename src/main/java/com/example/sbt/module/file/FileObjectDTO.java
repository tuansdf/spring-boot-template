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
public class FileObjectDTO {

    private UUID id;
    private String filePath;
    private String previewFilePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Instant createdAt;
    private Instant updatedAt;

}
