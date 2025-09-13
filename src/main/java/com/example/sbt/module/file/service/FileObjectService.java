package com.example.sbt.module.file.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.SearchFileRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileObjectService {
    FileObjectDTO uploadFile(MultipartFile file, String dirPath, RequestContext requestContext);

    FileObjectDTO uploadFile(byte[] file, String dirPath, String filename, RequestContext requestContext);

    FileObjectPendingDTO createPendingUpload(String filename, String dirPath, RequestContext requestContext);

    FileObjectPendingDTO createPendingUpload(String mimeType, RequestContext requestContext);

    FileObjectDTO savePendingUpload(UUID id, RequestContext requestContext);

    FileObjectDTO getFileById(UUID id, RequestContext requestContext);

    FileObjectDTO setFileUrls(FileObjectDTO dto);

    void deleteFilesByIds(List<UUID> ids, UUID userId);

    void deleteExpiredPendingUpload();

    @Async
    void deleteExpiredPendingUploadAsync();

    PaginationData<FileObjectDTO> search(SearchFileRequest requestDTO, boolean isCount);
}
