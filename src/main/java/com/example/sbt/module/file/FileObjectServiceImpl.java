package com.example.sbt.module.file;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.io.FileUtils;
import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class FileObjectServiceImpl implements FileObjectService {

    private final CommonMapper commonMapper;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;
    private final FileObjectTempRepository fileObjectTempRepository;

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
        result.setCreatedBy(RequestHolder.getContext().getUserId());
        return commonMapper.toDTO(fileObjectRepository.save(result));
    }

    @Override
    public FileObjectTempDTO createTempUploadFile(String dirPath, FileType fileType) {
        ObjectKey objectKey = uploadFileService.createPresignedPutUrl(dirPath, fileType);
        if (objectKey == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObjectTemp result = new FileObjectTemp();
        result.setFilePath(objectKey.getFilePath());
        result.setUploadFileUrl(objectKey.getFileUrl());
        result.setFileType(fileType.getMimeType());
        result.setFileName(objectKey.getFileName());
        result.setCreatedBy(RequestHolder.getContext().getUserId());
        return commonMapper.toDTO(fileObjectTempRepository.save(result));
    }

    @Override
    public FileObjectTempDTO createTempUploadFile(String mimeType) {
        FileType fileType = FileType.fromMimeType(mimeType);
        if (fileType == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return createTempUploadFile(null, fileType);
    }

    @Override
    public FileObjectDTO saveTempUploadFile(UUID id) {
        FileObjectTempDTO tempDTO = fileObjectTempRepository.findTopByIdAndCreatedBy(id, RequestHolder.getContext().getUserId()).map(commonMapper::toDTO).orElse(null);
        if (tempDTO == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        FileType fileType = FileType.fromMimeType(tempDTO.getFileType());
        if (fileType == null) {
            uploadFileService.deleteFile(tempDTO.getFilePath());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        byte[] headerBytes = uploadFileService.getFileHeaderBytes(tempDTO.getFilePath());
        if (headerBytes == null || headerBytes.length == 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isFileValid = FileUtils.validateFileType(headerBytes, Lists.newArrayList(fileType));
        if (!isFileValid) {
            uploadFileService.deleteFile(tempDTO.getFilePath());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        HeadObjectResponse metadata = uploadFileService.getFileMetadata(tempDTO.getFilePath());
        FileObject result = new FileObject();
        result.setFileName(tempDTO.getFileName());
        result.setFileSize(metadata.contentLength());
        result.setFilePath(tempDTO.getFilePath());
        result.setFileType(tempDTO.getFileType());
        result.setCreatedBy(tempDTO.getCreatedBy());
        result = fileObjectRepository.save(result);
        fileObjectTempRepository.deleteById(tempDTO.getId());
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO getFileById(UUID id) {
        FileObjectDTO result = fileObjectRepository.findTopByIdAndCreatedBy(id, RequestHolder.getContext().getUserId()).map(commonMapper::toDTO).orElse(null);
        if (result == null) return null;
        result.setFileUrl(uploadFileService.createPresignedGetUrl(result.getFilePath()));
        result.setPreviewFileUrl(uploadFileService.createPresignedGetUrl(result.getPreviewFilePath()));
        return result;
    }

    @Override
    public void deleteFilesByIds(Set<UUID> ids) {
        Set<String> filePaths = fileObjectRepository.findAllPathsByIdInAndCreatedBy(ids, RequestHolder.getContext().getUserId());
        fileObjectRepository.deleteAllByIdInAndCreatedBy(ids, RequestHolder.getContext().getUserId());
        uploadFileService.deleteFiles(filePaths);
    }

}
