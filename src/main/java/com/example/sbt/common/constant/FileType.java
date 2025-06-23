package com.example.sbt.common.constant;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum FileType {
    PNG("png", "image/png"),
    JPEG("jpeg", "image/jpeg"),
    WEBP("webp", "image/webp"),
    GIF("gif", "image/gif"),
    MP3("mp3", "audio/mpeg"),
    MP4("mp4", "video/mp4"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    PDF("pdf", "application/pdf"),
    TXT("txt", "text/plain"),
    CSV("csv", "text/csv");

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static FileType fromExtension(String extension) {
        if (StringUtils.isBlank(extension)) return null;
        extension = extension.toLowerCase();
        if ("jpg".equals(extension)) {
            extension = "jpeg";
        }
        for (FileType fileType : FileType.values()) {
            if (fileType.extension.equals(extension)) {
                return fileType;
            }
        }
        return null;
    }

    public static FileType fromMimeType(String mimeType) {
        if (StringUtils.isBlank(mimeType)) return null;
        mimeType = mimeType.toLowerCase();
        for (FileType fileType : FileType.values()) {
            if (fileType.mimeType.equals(mimeType)) {
                return fileType;
            }
        }
        return null;
    }

}
