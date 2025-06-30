package com.example.sbt.module.file.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.SearchFileRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileObjectService {

    FileObjectDTO uploadFile(MultipartFile file, String filePath);

    FileObjectPendingDTO createPendingFileUpload(String mimeType, String dirPath);

    FileObjectPendingDTO createPendingFileUpload(String mimeType);

    FileObjectDTO savePendingFileUpload(UUID id);

    FileObjectDTO getFileById(UUID id);

    void deleteFilesByIds(List<UUID> ids);

    PaginationData<FileObjectDTO> search(SearchFileRequestDTO requestDTO, boolean isCount);

}
