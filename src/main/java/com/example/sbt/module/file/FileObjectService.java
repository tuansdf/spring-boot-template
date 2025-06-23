package com.example.sbt.module.file;

import com.example.sbt.common.constant.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public interface FileObjectService {

    FileObjectDTO uploadFile(MultipartFile file, String filePath);

    FileObjectTempDTO createTempUploadFile(String dirPath, FileType fileType);

    FileObjectTempDTO createTempUploadFile(String mimeType);

    FileObjectDTO saveTempUploadFile(UUID id);

    FileObjectDTO getFileById(UUID id);

    void deleteFilesByIds(Set<UUID> ids);

}
