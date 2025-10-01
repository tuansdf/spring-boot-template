package com.example.sbt.module.file.service;

import com.example.sbt.common.dto.TempFile;
import com.example.sbt.module.file.dto.ObjectKey;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.io.InputStream;
import java.util.List;

public interface UploadFileService {
    String uploadFile(byte[] file, String filePath, String filename);

    String uploadFile(MultipartFile file, String filePath, String filename);

    String createPresignedGetUrl(String filePath, String filename, Long seconds);

    String createPresignedGetUrl(String filePath, String filename);

    ObjectKey createPresignedPutUrl(String dirPath, String filename, Long seconds);

    ObjectKey createPresignedPutUrl(String dirPath, String filename);

    InputStream getFileStream(String filePath);

    TempFile getFile(String filePath);

    byte[] getFileHeaderBytes(String filePath, Integer size);

    HeadObjectResponse getFileMetadata(String filePath);

    byte[] getFileHeaderBytes(String filePath);

    boolean existsFile(String filePath);

    void deleteFile(String filePath);

    void deleteFiles(List<String> filePaths);
}
