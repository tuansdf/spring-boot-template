package com.example.sbt.module.file.service;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.exception.NoRollbackException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.FileUtils;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.ObjectKey;
import com.example.sbt.module.file.dto.SearchFileRequestDTO;
import com.example.sbt.module.file.entity.FileObject;
import com.example.sbt.module.file.entity.FileObjectPending;
import com.example.sbt.module.file.repository.FileObjectPendingRepository;
import com.example.sbt.module.file.repository.FileObjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class, dontRollbackOn = NoRollbackException.class)
public class FileObjectServiceImpl implements FileObjectService {

    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;
    private final FileObjectPendingRepository fileObjectPendingRepository;

    @Override
    public FileObjectDTO uploadFile(MultipartFile file, String dirPath) {
        if (file == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isFileValid = FileUtils.validateFileType(file);
        if (!isFileValid) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String filePath = uploadFileService.uploadFile(file, dirPath, file.getOriginalFilename());
        if (filePath == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setFilePath(filePath);
        result.setFileName(FileUtils.truncateFileName(file.getOriginalFilename()));
        result.setFileType(file.getContentType());
        result.setFileSize(file.getSize());
        result.setCreatedBy(RequestContext.get().getUserId());
        return commonMapper.toDTO(fileObjectRepository.save(result));
    }

    @Override
    public FileObjectPendingDTO createPendingFileUpload(String mimeType, String dirPath) {
        final long EXPIRES_SECONDS = 10L * 60L;
        FileType fileType = FileType.fromMimeType(mimeType);
        if (fileType == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        ObjectKey objectKey = uploadFileService.createPresignedPutUrl(dirPath, fileType, EXPIRES_SECONDS);
        if (objectKey == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObjectPending result = new FileObjectPending();
        result.setFilePath(objectKey.getFilePath());
        result.setFileType(fileType.getMimeType());
        result.setFileName(objectKey.getFileName());
        result.setExpiresAt(Instant.now().plusSeconds(EXPIRES_SECONDS));
        result.setCreatedBy(RequestContext.get().getUserId());
        FileObjectPendingDTO dto = commonMapper.toDTO(fileObjectPendingRepository.save(result));
        dto.setFileUploadUrl(objectKey.getFileUrl());
        return dto;
    }

    @Override
    public FileObjectPendingDTO createPendingFileUpload(String mimeType) {
        return createPendingFileUpload(mimeType, null);
    }

    private void deletePendingFileUpload(UUID id, String filePath) {
        fileObjectPendingRepository.deleteById(id);
        uploadFileService.deleteFile(filePath);
    }

    @Override
    public FileObjectDTO savePendingFileUpload(UUID id) {
        FileObjectPendingDTO pendingDTO = fileObjectPendingRepository.findTopByIdAndCreatedBy(id, RequestContext.get().getUserId()).map(commonMapper::toDTO).orElse(null);
        if (pendingDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        FileType fileType = FileType.fromMimeType(pendingDTO.getFileType());
        if (fileType == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        HeadObjectResponse metadata = uploadFileService.getFileMetadata(pendingDTO.getFilePath());
        if (metadata == null || metadata.contentLength() <= 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        byte[] headerBytes = uploadFileService.getFileHeaderBytes(pendingDTO.getFilePath());
        if (headerBytes == null || headerBytes.length == 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isFileValid = FileUtils.validateFileType(headerBytes, List.of(fileType));
        if (!isFileValid) {
            deletePendingFileUpload(id, pendingDTO.getFilePath());
            throw new NoRollbackException(HttpStatus.BAD_REQUEST);
        }
        FileObject result = new FileObject();
        result.setFileName(pendingDTO.getFileName());
        result.setFileSize(metadata.contentLength());
        result.setFilePath(pendingDTO.getFilePath());
        result.setFileType(pendingDTO.getFileType());
        result.setCreatedBy(pendingDTO.getCreatedBy());
        result = fileObjectRepository.save(result);
        fileObjectPendingRepository.deleteById(pendingDTO.getId());
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO getFileById(UUID id) {
        FileObjectDTO result = fileObjectRepository.findTopByIdAndCreatedBy(id, RequestContext.get().getUserId()).map(commonMapper::toDTO).orElse(null);
        if (result == null) return null;
        result.setFileUrl(uploadFileService.createPresignedGetUrl(result.getFilePath()));
        result.setPreviewFileUrl(uploadFileService.createPresignedGetUrl(result.getPreviewFilePath()));
        return result;
    }

    @Override
    public void deleteFilesByIds(List<UUID> ids) {
        List<String> filePaths = fileObjectRepository.findAllPathsByIdInAndCreatedBy(ids, RequestContext.get().getUserId());
        fileObjectRepository.deleteAllByIdInAndCreatedBy(ids, RequestContext.get().getUserId());
        uploadFileService.deleteFiles(filePaths);
    }

    @Override
    public PaginationData<FileObjectDTO> search(SearchFileRequestDTO requestDTO, boolean isCount) {
        PaginationData<FileObjectDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<FileObjectDTO> executeSearch(SearchFileRequestDTO requestDTO, boolean isCount) {
        PaginationData<FileObjectDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select fo.* ");
        }
        builder.append(" from file_object fo ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getFileType())) {
            builder.append(" and fo.file_type = :fileType ");
            params.put("fileType", requestDTO.getFileType().trim());
        }
        if (requestDTO.getFileSizeFrom() != null) {
            builder.append(" and fo.file_size >= :fileSizeFrom ");
            params.put("fileSizeFrom", requestDTO.getFileSizeFrom());
        }
        if (requestDTO.getFileSizeTo() != null) {
            builder.append(" and fo.file_size <= :fileSizeTo ");
            params.put("fileSizeTo", requestDTO.getFileSizeTo());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and fo.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and fo.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            List<String> orderBys = List.of("created_at", "file_type", "file_size");
            List<String> orderDirections = List.of("asc", "desc");
            if (orderBys.contains(requestDTO.getOrderBy())) {
                builder.append(" order by fo.").append(requestDTO.getOrderBy()).append(" ");
                if (orderDirections.contains(requestDTO.getOrderDirection())) {
                    builder.append(" ").append(requestDTO.getOrderDirection()).append(" ");
                } else {
                    builder.append(" asc ");
                }
                builder.append(" , fo.id desc ");
            } else {
                builder.append(" order by fo.id desc ");
            }
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.FILE_SEARCH);
            SQLHelper.setParams(query, params);
            List<FileObjectDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

}
