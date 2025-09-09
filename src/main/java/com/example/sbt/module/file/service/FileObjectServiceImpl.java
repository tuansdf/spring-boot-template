package com.example.sbt.module.file.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.exception.NoRollbackException;
import com.example.sbt.core.helper.SQLHelper;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.ObjectKey;
import com.example.sbt.module.file.dto.SearchFileRequest;
import com.example.sbt.module.file.entity.FileObject;
import com.example.sbt.module.file.entity.FileObjectPending;
import com.example.sbt.module.file.repository.FileObjectPendingRepository;
import com.example.sbt.module.file.repository.FileObjectRepository;
import com.example.sbt.shared.constant.FileType;
import com.example.sbt.shared.util.CommonUtils;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.DateUtils;
import com.example.sbt.shared.util.FileUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class, dontRollbackOn = NoRollbackException.class)
public class FileObjectServiceImpl implements FileObjectService {
    private final SQLHelper sqlHelper;
    private final CommonMapper commonMapper;
    private final EntityManager entityManager;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;
    private final FileObjectPendingRepository fileObjectPendingRepository;

    @Override
    public FileObjectDTO uploadFile(MultipartFile file, String dirPath, RequestContext requestContext) {
        if (file == null || file.isEmpty()) {
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
        result.setFilename(FileUtils.truncateFilename(file.getOriginalFilename()));
        result.setFileType(file.getContentType());
        result.setFileSize(file.getSize());
        result.setCreatedBy(requestContext.getUserId());
        return commonMapper.toDTO(fileObjectRepository.save(result));
    }

    @Override
    public FileObjectDTO uploadFile(byte[] file, String dirPath, String filename, RequestContext requestContext) {
        if (file == null || file.length == 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        FileType fileType = FileUtils.validateFileType(file);
        if (fileType == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String filePath = uploadFileService.uploadFile(file, dirPath, filename);
        if (filePath == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setFilePath(filePath);
        result.setFilename(FileUtils.truncateFilename(filename));
        result.setFileType(fileType.getMimeType());
        result.setFileSize(ConversionUtils.safeToLong(file.length));
        result.setCreatedBy(requestContext.getUserId());
        return commonMapper.toDTO(fileObjectRepository.save(result));
    }

    @Override
    public FileObjectPendingDTO createPendingUpload(String filename, String dirPath, RequestContext requestContext) {
        final long EXPIRES_SECONDS = 10L * 60L;
        String extension = FileUtils.getFileExtension(filename);
        FileType fileType = FileType.fromExtension(extension);
        if (fileType == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        ObjectKey objectKey = uploadFileService.createPresignedPutUrl(dirPath, filename, EXPIRES_SECONDS);
        if (objectKey == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObjectPending result = new FileObjectPending();
        result.setFilePath(objectKey.getFilePath());
        result.setFileType(fileType.getMimeType());
        result.setFilename(FileUtils.truncateFilename(filename));
        result.setExpiresAt(Instant.now().plusSeconds(EXPIRES_SECONDS));
        result.setCreatedBy(requestContext.getUserId());
        FileObjectPendingDTO dto = commonMapper.toDTO(fileObjectPendingRepository.save(result));
        dto.setFileUploadUrl(objectKey.getFileUrl());
        return dto;
    }

    @Override
    public FileObjectPendingDTO createPendingUpload(String mimeType, RequestContext requestContext) {
        return createPendingUpload(mimeType, null, requestContext);
    }

    private void deletePendingUpload(UUID id, String filePath) {
        uploadFileService.deleteFile(filePath);
        fileObjectPendingRepository.deleteById(id);
    }

    @Override
    public FileObjectDTO savePendingUpload(UUID id, RequestContext requestContext) {
        FileObjectPendingDTO pendingDTO = fileObjectPendingRepository.findTopByIdAndCreatedBy(id, requestContext.getUserId()).map(commonMapper::toDTO).orElse(null);
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
        FileType validFileType = FileUtils.validateFileType(headerBytes, List.of(fileType));
        if (validFileType == null) {
            deletePendingUpload(id, pendingDTO.getFilePath());
            throw new NoRollbackException(HttpStatus.BAD_REQUEST);
        }
        FileObject result = new FileObject();
        result.setFilename(pendingDTO.getFilename());
        result.setFileSize(metadata.contentLength());
        result.setFilePath(pendingDTO.getFilePath());
        result.setFileType(pendingDTO.getFileType());
        result.setCreatedBy(pendingDTO.getCreatedBy());
        result = fileObjectRepository.save(result);
        fileObjectPendingRepository.deleteById(pendingDTO.getId());
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO getFileById(UUID id, RequestContext requestContext) {
        FileObjectDTO result = fileObjectRepository.findTopByIdAndCreatedBy(id, requestContext.getUserId()).map(commonMapper::toDTO).orElse(null);
        if (result == null) return null;
        result.setFileUrl(uploadFileService.createPresignedGetUrl(result.getFilePath(), result.getFilename()));
        result.setPreviewFileUrl(uploadFileService.createPresignedGetUrl(result.getPreviewFilePath(), result.getFilename()));
        return result;
    }

    @Override
    public FileObjectDTO setFileUrls(FileObjectDTO dto) {
        if (dto == null) return null;
        dto.setFileUrl(uploadFileService.createPresignedGetUrl(dto.getFilePath(), dto.getFilename()));
        dto.setPreviewFileUrl(uploadFileService.createPresignedGetUrl(dto.getPreviewFilePath(), dto.getFilename()));
        return dto;
    }

    @Override
    public void deleteFilesByIds(List<UUID> ids, UUID userId) {
        List<String> filePaths = fileObjectRepository.findAllFilePathsByIdInAndCreatedBy(ids, userId);
        fileObjectRepository.deleteByIdInAndCreatedBy(ids, userId);
        uploadFileService.deleteFiles(filePaths);
    }

    @Override
    public void deleteExpiredPendingUpload() {
        Instant now = Instant.now();
        List<String> filePaths = fileObjectPendingRepository.findAllFilePathsByExpiresAtBefore(now);
        fileObjectPendingRepository.deleteByExpiresAtBefore(now);
        uploadFileService.deleteFiles(filePaths);
    }

    @Async
    @Override
    public void deleteExpiredPendingUploadAsync() {
        deleteExpiredPendingUpload();
    }

    @Override
    public PaginationData<FileObjectDTO> search(SearchFileRequest requestDTO, boolean isCount) {
        PaginationData<FileObjectDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<FileObjectDTO> executeSearch(SearchFileRequest requestDTO, boolean isCount) {
        requestDTO.setOrderBy(CommonUtils.inListOrNull(requestDTO.getOrderBy(), List.of("created_at", "file_type", "file_size")));
        requestDTO.setOrderDirection(CommonUtils.inListOrNull(requestDTO.getOrderDirection(), List.of("asc", "desc")));
        PaginationData<FileObjectDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select ");
            builder.append(" fo.id, fo.file_path, fo.preview_file_path, fo.filename, fo.file_type, ");
            builder.append(" fo.file_size, fo.created_by, fo.created_at, fo.updated_at ");
        }
        builder.append(" from file_object fo ");
        builder.append(" where 1=1 ");
        if (StringUtils.isNotBlank(requestDTO.getFileType())) {
            builder.append(" and fo.file_type = ? ");
            params.add(requestDTO.getFileType().trim());
        }
        if (requestDTO.getFileSizeFrom() != null) {
            builder.append(" and fo.file_size >= ? ");
            params.add(requestDTO.getFileSizeFrom());
        }
        if (requestDTO.getFileSizeTo() != null) {
            builder.append(" and fo.file_size < ? ");
            params.add(requestDTO.getFileSizeTo());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and fo.created_at >= ? ");
            params.add(requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and fo.created_at < ? ");
            params.add(requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by ");
            builder.append(CommonUtils.joinWhenNoNull(" fo.", requestDTO.getOrderBy(), " ", requestDTO.getOrderDirection(), ", "));
            builder.append(" fo.id desc ");
            builder.append(" limit ? offset ? ");
            sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(sqlHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            List<Object[]> objects = query.getResultList();
            List<FileObjectDTO> items = objects.stream().map(x -> {
                FileObjectDTO dto = new FileObjectDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setFilePath(ConversionUtils.toString(x[1]));
                dto.setPreviewFilePath(ConversionUtils.toString(x[2]));
                dto.setFilename(ConversionUtils.toString(x[3]));
                dto.setFileType(ConversionUtils.toString(x[4]));
                dto.setFileSize(ConversionUtils.toLong(x[5]));
                dto.setCreatedBy(ConversionUtils.toUUID(x[6]));
                dto.setCreatedAt(DateUtils.toInstant(x[7]));
                dto.setUpdatedAt(DateUtils.toInstant(x[8]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }
}
