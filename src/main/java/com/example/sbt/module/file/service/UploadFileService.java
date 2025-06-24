package com.example.sbt.module.file.service;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.module.file.dto.ObjectKey;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.Set;

public interface UploadFileService {

    String uploadFile(byte[] file, String filePath, String fileName);

    String uploadFile(MultipartFile file, String filePath, String fileName);

    String createPresignedGetUrl(String filePath, Long seconds);

    String createPresignedGetUrl(String filePath);

    ObjectKey createPresignedPutUrl(String dirPath, FileType fileType, Long seconds);

    ObjectKey createPresignedPutUrl(String dirPath, FileType fileType);

    byte[] getFile(String filePath);

    byte[] getFileHeaderBytes(String filePath, Integer size);

    HeadObjectResponse getFileMetadata(String filePath);

    byte[] getFileHeaderBytes(String filePath);

    void deleteFile(String filePath);

    void deleteFiles(Set<String> filePaths);

}
