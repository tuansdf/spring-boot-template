package com.example.sbt.module.file.dto;

import com.example.sbt.common.constant.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectKey {
    private String dirPath;
    private String originalFilename;
    private String filename;
    private String filePath;
    private String fileUrl;
    private FileType fileType;
}

