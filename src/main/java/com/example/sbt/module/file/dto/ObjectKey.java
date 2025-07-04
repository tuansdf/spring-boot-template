package com.example.sbt.module.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class ObjectKey {
    private String dirPath;
    private String originalFilename;
    private String filename;
    private String filePath;
    private String fileUrl;
}

