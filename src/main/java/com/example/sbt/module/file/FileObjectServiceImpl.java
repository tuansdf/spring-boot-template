package com.example.sbt.module.file;

import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.CommonUtils;
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

    private static final int PRIMARY_WIDTH = 2000;
    private static final int THUMBNAIL_WIDTH = 600;
    private static final FileType[] IMAGE_FILE_TYPES = {FileType.JPEG, FileType.JPG, FileType.PNG, FileType.WEBP};
    private final CommonMapper commonMapper;
    private final UploadFileService uploadFileService;
    private final FileObjectRepository fileObjectRepository;

    @Override
    public FileObjectDTO upload(byte[] file, String filePath) {
        String url = uploadFileService.upload(filePath, file);
        if (url == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FileObject result = new FileObject();
        result.setFileUrl(url);
        result = fileObjectRepository.save(result);
        return commonMapper.toDTO(result);
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file, String dirPath, Integer thumbnailWidth) {
        try {
            if (StringUtils.isBlank(dirPath)) {
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
            if (!FileUtils.validateFileType(file, IMAGE_FILE_TYPES)) {
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }

            dirPath = CommonUtils.trim(dirPath, '/');
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (StringUtils.isBlank(extension)) {
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
            UUID id = RandomUtils.Secure.generateTimeBasedUUID();
            String filePath = dirPath.concat("/").concat(id.toString()).concat(".").concat(extension);

            byte[] fileBytes = file.getBytes();
            {
                byte[] compressedFileBytes = ImageUtils.compressImageToBytes(fileBytes,
                        ImageUtils.Options.builder().width(PRIMARY_WIDTH).format(extension).quality(0.8F).build());
                if (compressedFileBytes != null) {
                    fileBytes = compressedFileBytes;
                }
            }
            String url = uploadFileService.upload(filePath, fileBytes);
            if (url == null) {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String thumbnailUrl = null;
            if (thumbnailWidth != null && thumbnailWidth > 0) {
                String thumbnailFilePath = dirPath.concat("/").concat(id.toString()).concat("_thumbnail.").concat(FileType.JPG.getExtension());
                byte[] thumbnailFileBytes = ImageUtils.compressImageToBytes(fileBytes,
                        ImageUtils.Options.builder().width(thumbnailWidth).format(FileType.JPG.getExtension()).quality(0.8F).build());
                if (thumbnailFileBytes != null) {
                    thumbnailUrl = uploadFileService.upload(thumbnailFilePath, thumbnailFileBytes);
                }
            }

            FileObject result = new FileObject();
            result.setId(id);
            result.setFileUrl(url);
            result.setPreviewFileUrl(thumbnailUrl);
            result = fileObjectRepository.save(result);
            return commonMapper.toDTO(result);
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file, String dirPath) {
        return uploadImage(file, dirPath, THUMBNAIL_WIDTH);
    }

    @Override
    public FileObjectDTO uploadImage(MultipartFile file) {
        return uploadImage(file, null, THUMBNAIL_WIDTH);
    }

}
