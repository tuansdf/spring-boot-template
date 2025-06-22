package com.example.sbt.module.file;

import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.io.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class FileObjectServiceImpl implements FileObjectService {

    private final CommonMapper commonMapper;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;

    @Override
    public FileObjectDTO uploadFile(MultipartFile file, String dirPath) {
        if (file == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        boolean isFileValid = FileUtils.validateFileType(file, null);
        if (!isFileValid) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String filePath = uploadFileService.uploadFile(file, dirPath, file.getOriginalFilename());
        if (filePath == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setFilePath(filePath);
        result.setFileName(FileUtils.truncateFileName(file.getOriginalFilename(), 255));
        result.setFileType(file.getContentType());
        result.setFileSize(file.getSize());
        result.setCreatedBy(RequestHolder.getContext().getUserId());
        return commonMapper.toDTO(fileObjectRepository.save(result));
    }

    @Override
    public FileObjectDTO getFileById(UUID id) {
        FileObjectDTO result = fileObjectRepository.findTopByIdAndCreatedBy(id, RequestHolder.getContext().getUserId()).map(commonMapper::toDTO).orElse(null);
        if (result == null) return null;
        result.setFileUrl(uploadFileService.createPresignedGetUrl(result.getFilePath(), null));
        result.setPreviewFileUrl(uploadFileService.createPresignedGetUrl(result.getPreviewFilePath(), null));
        return result;
    }

    @Override
    public void deleteFilesByIds(Set<UUID> ids) {
        Set<String> filePaths = fileObjectRepository.findAllPathsByIdInAndCreatedBy(ids, RequestHolder.getContext().getUserId());
        fileObjectRepository.deleteAllByIdInAndCreatedBy(ids, RequestHolder.getContext().getUserId());
        uploadFileService.deleteFiles(filePaths);
    }

}
