package com.example.sbt.module.file;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.RandomUtils;
import com.example.sbt.common.util.io.FileUtils;
import com.example.sbt.common.util.io.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileObjectServiceImpl implements FileObjectService {

    private static final int PRIMARY_WIDTH = 4000;
    private static final int THUMBNAIL_WIDTH = 600;
    private static final FileType[] IMAGE_FILE_TYPES = {FileType.JPEG, FileType.JPG, FileType.PNG, FileType.WEBP};

    private final CommonMapper commonMapper;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;

    @Override
    public FileObjectDTO upload(MultipartFile file, String dirPath) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isBlank(extension)) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        dirPath = StringUtils.strip(ConversionUtils.safeTrim(dirPath), "/");
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        String filePath = StringUtils.strip(dirPath.concat("/").concat(id.toString()).concat(".").concat(extension), "/");
        filePath = uploadFileService.upload(file.getBytes(), filePath);
        if (filePath == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setId(id);
        result.setFilePath(filePath);
        result.setFileName(file.getOriginalFilename());
        result.setFileType(file.getContentType());
        result.setFileSize(file.getSize());
        result.setCreatedBy(RequestHolder.getContext().getUserId());
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file, String dirPath, Integer thumbnailWidth) throws IOException {
        if (!FileUtils.validateFileType(file, IMAGE_FILE_TYPES)) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isBlank(extension)) {
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        dirPath = StringUtils.strip(ConversionUtils.safeTrim(dirPath), "/");
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        String filePath = StringUtils.strip(dirPath.concat("/").concat(id.toString()).concat(".").concat(extension), "/");

        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        long fileSize = file.getSize();
        byte[] fileBytes = file.getBytes();
        {
            byte[] compressedFileBytes = ImageUtils.compressImageToBytes(fileBytes,
                    ImageUtils.Options.builder().width(PRIMARY_WIDTH).format(extension).quality(0.8F).build());
            if (compressedFileBytes != null) {
                fileSize = fileBytes.length;
                fileBytes = compressedFileBytes;
            }
        }
        filePath = uploadFileService.upload(fileBytes, filePath);
        if (filePath == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String thumbnailPath = null;
        if (thumbnailWidth != null && thumbnailWidth > 0) {
            String thumbnailFilePath = StringUtils.strip(dirPath.concat("/").concat(id.toString()).concat("_thumbnail.").concat(FileType.JPG.getExtension()), "/");
            fileBytes = ImageUtils.compressImageToBytes(fileBytes,
                    ImageUtils.Options.builder().width(thumbnailWidth).format(FileType.JPG.getExtension()).quality(0.8F).build());
            if (fileBytes != null) {
                thumbnailPath = uploadFileService.upload(fileBytes, thumbnailFilePath);
            }
        }

        FileObject result = new FileObject();
        result.setId(id);
        result.setFilePath(filePath);
        result.setPreviewFilePath(thumbnailPath);
        result.setFileName(fileName);
        result.setFileType(fileType);
        result.setFileSize(fileSize);
        result.setCreatedBy(RequestHolder.getContext().getUserId());
        result = fileObjectRepository.save(result);
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file, String dirPath) throws IOException {
        return uploadImage(file, dirPath, THUMBNAIL_WIDTH);
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "", THUMBNAIL_WIDTH);
    }

}
