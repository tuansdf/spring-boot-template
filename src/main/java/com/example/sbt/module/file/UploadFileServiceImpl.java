package com.example.sbt.module.file;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.constant.FileType;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.RandomUtils;
import com.example.sbt.common.util.io.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private static final String PATH_SEPARATOR = "/";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final long DEFAULT_PRESIGN_GET_SECONDS = 24L * 60L * 60L;
    private static final long DEFAULT_PRESIGN_PUT_SECONDS = 10L * 60L;

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private ObjectKey cleanObjectKey(String dirPath, String originalFileName) {
        dirPath = ConversionUtils.safeTrim(dirPath);
        originalFileName = FileUtils.truncateFileName(FileUtils.cleanFileName(originalFileName));
        String extension = FileUtils.getFileExtension(originalFileName);
        String fileName = RandomUtils.Secure.generateUUID().toString();
        if (StringUtils.isNotBlank(extension)) {
            fileName = fileName.concat(EXTENSION_SEPARATOR).concat(extension);
        }
        String filePath = FileUtils.cleanFilePath(dirPath.concat(PATH_SEPARATOR).concat(fileName));
        return ObjectKey.builder()
                .dirPath(dirPath)
                .originalFileName(originalFileName)
                .fileName(fileName)
                .filePath(filePath)
                .build();
    }

    @Override
    public String uploadFile(byte[] file, String dirPath, String fileName) {
        try {
            if (file == null) return null;
            ObjectKey objectKey = cleanObjectKey(dirPath, fileName);
            if (StringUtils.isBlank(objectKey.getOriginalFileName())
                    || StringUtils.isBlank(objectKey.getFileName())
                    || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .contentDisposition(ContentDisposition.attachment().filename(objectKey.getOriginalFileName(), StandardCharsets.UTF_8).build().toString())
                    .key(objectKey.getFilePath())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
            return objectKey.getFilePath();
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String dirPath, String fileName) {
        try {
            if (file == null) return null;
            ObjectKey objectKey = cleanObjectKey(dirPath, fileName);
            if (StringUtils.isBlank(objectKey.getOriginalFileName())
                    || StringUtils.isBlank(objectKey.getFileName())
                    || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .contentDisposition(ContentDisposition.attachment().filename(objectKey.getOriginalFileName(), StandardCharsets.UTF_8).build().toString())
                    .key(objectKey.getFilePath())
                    .build();
            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            }
            return objectKey.getFilePath();
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String createPresignedGetUrl(String filePath, Long seconds) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            if (seconds == null || seconds <= 0L) {
                seconds = DEFAULT_PRESIGN_GET_SECONDS;
            }
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(seconds))
                    .getObjectRequest(getObjectRequest)
                    .build();
            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("createPresignedGetUrl ", e);
            return null;
        }
    }

    @Override
    public String createPresignedGetUrl(String filePath) {
        return createPresignedGetUrl(filePath, null);
    }

    @Override
    public ObjectKey createPresignedPutUrl(String dirPath, FileType fileType, Long seconds) {
        try {
            if (fileType == null) return null;
            ObjectKey objectKey = cleanObjectKey(dirPath, "_".concat(EXTENSION_SEPARATOR).concat(fileType.getExtension()));
            if (StringUtils.isBlank(objectKey.getOriginalFileName())
                    || StringUtils.isBlank(objectKey.getFileName())
                    || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            if (seconds == null || seconds <= 0L) {
                seconds = DEFAULT_PRESIGN_PUT_SECONDS;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(objectKey.getFilePath())
                    .contentType(fileType.getMimeType())
                    .ifNoneMatch("*")
                    .build();
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(seconds))
                    .putObjectRequest(putObjectRequest)
                    .build();
            String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
            objectKey.setFileUrl(presignedUrl);
            return objectKey;
        } catch (Exception e) {
            log.error("createPresignedPutUrl ", e);
            return null;
        }
    }

    @Override
    public ObjectKey createPresignedPutUrl(String dirPath, FileType fileType) {
        return createPresignedPutUrl(dirPath, fileType, null);
    }

    @Override
    public byte[] getFile(String filePath) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath)
                    .build();
            return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
        } catch (Exception e) {
            log.error("getFile ", e);
            return null;
        }
    }

    @Override
    public byte[] getFileHeaderBytes(String filePath, Integer size) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            if (size == null || size <= 0) {
                size = 2048;
            }
            String rangeHeader = "bytes=0-" + (size - 1);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath)
                    .range(rangeHeader)
                    .build();
            return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
        } catch (Exception e) {
            log.error("getFileHeaderBytes ", e);
            return null;
        }
    }

    @Override
    public byte[] getFileHeaderBytes(String filePath) {
        return getFileHeaderBytes(filePath, null);
    }

    @Override
    public HeadObjectResponse getFileMetadata(String filePath) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath)
                    .build();
            HeadObjectResponse response = s3Client.headObject(request);
            if (!response.hasMetadata()) return null;
            return response;
        } catch (Exception e) {
            log.error("getFileMetadata ", e);
            return null;
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            if (StringUtils.isBlank(filePath)) return;
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("deleteFile ", e);
        }
    }

    @Override
    public void deleteFiles(Set<String> filePaths) {
        try {
            if (CollectionUtils.isEmpty(filePaths)) return;
            filePaths = filePaths.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(filePaths)) return;
            List<ObjectIdentifier> objectIds = filePaths.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .delete(Delete.builder().objects(objectIds).build())
                    .build();
            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            log.error("deleteFiles ", e);
        }
    }

}
