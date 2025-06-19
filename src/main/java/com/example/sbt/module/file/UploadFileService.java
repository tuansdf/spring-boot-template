package com.example.sbt.module.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface UploadFileService {

    String uploadFile(byte[] file, String filePath, String fileName);

    String uploadFile(MultipartFile file, String filePath, String fileName);

    String createPresignedGetUrl(String filePath, Long seconds);

    byte[] getFile(String filePath);

    void deleteFile(String filePath);

    void deleteFiles(Set<String> filePaths);

}
