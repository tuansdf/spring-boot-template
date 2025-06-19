package com.example.sbt.module.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public interface FileObjectService {

    FileObjectDTO uploadFile(MultipartFile file, String filePath);

    FileObjectDTO getFileById(UUID id);

    void deleteFilesByIds(Set<UUID> ids);

}
