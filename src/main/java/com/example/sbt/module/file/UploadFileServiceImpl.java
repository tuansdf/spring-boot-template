package com.example.sbt.module.file;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private static final long DEFAULT_PRESIGNED_SECONDS = 24L * 60L * 60L;

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private String safeToFilePath(String filePath) {
        return ConversionUtils.safeTrim(StringUtils.strip(filePath
                .replaceAll("\\s*/\\s*", "/")
                .replaceAll("/+", "/"), "/"));
    }

    @Override
    public String uploadFile(byte[] file, String filePath, String fileName) {
        try {
            if (file == null || StringUtils.isBlank(filePath)) return null;
            filePath = safeToFilePath(filePath);
            if (StringUtils.isBlank(filePath)) return null;
            PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath);
            if (StringUtils.isNotBlank(fileName)) {
                putObjectRequestBuilder = putObjectRequestBuilder.contentDisposition("attachment; filename=\"".concat(fileName).concat("\""));
            }
            s3Client.putObject(putObjectRequestBuilder.build(), RequestBody.fromBytes(file));
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String filePath, String fileName) {
        try {
            if (file == null || StringUtils.isBlank(filePath)) return null;
            filePath = safeToFilePath(filePath);
            if (StringUtils.isBlank(filePath)) return null;
            PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                    .bucket(applicationProperties.getAwsS3Bucket())
                    .key(filePath);
            if (StringUtils.isNotBlank(fileName)) {
                putObjectRequestBuilder = putObjectRequestBuilder.contentDisposition("attachment; filename=\"".concat(fileName).concat("\""));
            }
            s3Client.putObject(putObjectRequestBuilder.build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String createPresignedGetUrl(String filePath, Long seconds) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            if (seconds == null || seconds <= 0L) seconds = DEFAULT_PRESIGNED_SECONDS;
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
                    .collect(Collectors.toList());
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
