package com.example.sbt.module.file;

import java.util.List;

public interface UploadFileService {

    String uploadFile(byte[] file, String filePath);

    String createPresignedGetUrl(String filePath, Long seconds);

    byte[] getFile(String filePath);

    void deleteFile(String filePath);

    void deleteFiles(List<String> filePaths);

}
