package com.example.sbt.module.file;

import com.example.sbt.common.dto.PaginationData;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public interface FileObjectService {

    FileObjectDTO uploadFile(MultipartFile file, String filePath);

    FileObjectPendingDTO createPendingFileUpload(String mimeType, String dirPath);

    FileObjectPendingDTO createPendingFileUpload(String mimeType);

    FileObjectDTO savePendingFileUpload(UUID id);

    FileObjectDTO getFileById(UUID id);

    void deleteFilesByIds(Set<UUID> ids);

    PaginationData<FileObjectDTO> search(SearchFileRequestDTO requestDTO, boolean isCount);

}
