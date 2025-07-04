package com.example.sbt.module.file.service;

import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.module.file.dto.ObjectKey;
import com.example.sbt.shared.constant.FileType;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.FileUtils;
import com.example.sbt.shared.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {
    private static final long DEFAULT_PRESIGN_GET_SECONDS = 24L * 60L * 60L;
    private static final long DEFAULT_PRESIGN_PUT_SECONDS = 10L * 60L;
    private final int OBJECT_KEY_BATCH_SIZE = 500;

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private ObjectKey cleanObjectKey(String dirPath, String originalFilename, FileType fileType) {
        dirPath = ConversionUtils.safeTrim(dirPath);
        originalFilename = FileUtils.truncateFilename(FileUtils.cleanFilename(originalFilename));
        if (fileType == null) {
            fileType = FileType.fromExtension(FileUtils.getFileExtension(originalFilename));
        }
        String filename = RandomUtils.secure().randomUUID().toString();
        if (fileType != null) {
            filename = FileUtils.getFilename(filename, fileType);
        }
        if (StringUtils.isBlank(originalFilename)) {
            originalFilename = filename;
        }
        String filePath = FileUtils.cleanFilePath(FileUtils.getFilePath(dirPath, filename));
        return ObjectKey.builder()
                .dirPath(dirPath)
                .originalFilename(originalFilename)
                .filename(filename)
                .filePath(filePath)
                .build();
    }

    @Override
    public String uploadFile(byte[] file, String dirPath, String filename) {
        try {
            if (file == null) return null;
            ObjectKey objectKey = cleanObjectKey(dirPath, filename, null);
            if (StringUtils.isBlank(objectKey.getFilename()) || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(objectKey.getFilePath())
                    .contentDisposition(ContentDisposition.attachment().filename(objectKey.getOriginalFilename(), StandardCharsets.UTF_8).build().toString())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
            return objectKey.getFilePath();
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String dirPath, String filename) {
        try {
            if (file == null) return null;
            ObjectKey objectKey = cleanObjectKey(dirPath, filename, null);
            if (StringUtils.isBlank(objectKey.getFilename()) || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(objectKey.getFilePath())
                    .contentDisposition(ContentDisposition.attachment().filename(objectKey.getOriginalFilename(), StandardCharsets.UTF_8).build().toString())
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
            ObjectKey objectKey = cleanObjectKey(dirPath, null, fileType);
            if (StringUtils.isBlank(objectKey.getFilename()) || StringUtils.isBlank(objectKey.getFilePath())) {
                return null;
            }
            if (seconds == null || seconds <= 0L) {
                seconds = DEFAULT_PRESIGN_PUT_SECONDS;
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(objectKey.getFilePath())
                    .contentType(fileType.getMimeType())
                    .ifNoneMatch("*") // api clients need to include this header: If-None-Match: *
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
    public void deleteFiles(List<String> filePaths) {
        try {
            if (CollectionUtils.isEmpty(filePaths)) return;
            List<ObjectIdentifier> objectIds = filePaths.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();
            int size = objectIds.size();
            for (int i = 0; i < size; i += OBJECT_KEY_BATCH_SIZE) {
                int to = Math.max(i + OBJECT_KEY_BATCH_SIZE, size);
                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(applicationProperties.getAwsS3Bucket())
                        .delete(Delete.builder().objects(objectIds.subList(i, to)).build())
                        .build();
                s3Client.deleteObjects(deleteObjectsRequest);
            }
        } catch (Exception e) {
            log.error("deleteFiles ", e);
        }
    }
}
