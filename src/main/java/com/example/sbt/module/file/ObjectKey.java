package com.example.sbt.module.file;

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
    private String originalFileName;
    private String fileName;
    private String filePath;
    private String fileUrl;

}

