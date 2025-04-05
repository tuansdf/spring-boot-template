package com.example.sbt.common.constant;

public enum FileType {
    PNG("png", "image/png"),
    JPEG("jpeg", "image/jpeg"),
    JPG("jpg", "image/jpeg"),
    WEBP("webp", "image/webp");

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static FileType fromExtension(String extension) {
        for (FileType fileType : FileType.values()) {
            if (fileType.extension.equalsIgnoreCase(extension)) {
                return fileType;
            }
        }
        return null;
    }

    public static FileType fromMimeType(String mimeType) {
        for (FileType fileType : FileType.values()) {
            if (fileType.mimeType.equalsIgnoreCase(mimeType)) {
                return fileType;
            }
        }
        return null;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
