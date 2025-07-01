package com.example.sbt.module.file.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.SearchFileRequestDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileObjectService {

    FileObjectDTO uploadFile(MultipartFile file, String filePath);

    FileObjectPendingDTO createPendingUpload(String mimeType, String dirPath);

    FileObjectPendingDTO createPendingUpload(String mimeType);

    FileObjectDTO savePendingUpload(UUID id);

    FileObjectDTO getFileById(UUID id);

    void deleteFilesByIds(List<UUID> ids, UUID userId);

    void deleteExpiredPendingUpload();

    @Async
    void deleteExpiredPendingUploadAsync();

    PaginationData<FileObjectDTO> search(SearchFileRequestDTO requestDTO, boolean isCount);

}
