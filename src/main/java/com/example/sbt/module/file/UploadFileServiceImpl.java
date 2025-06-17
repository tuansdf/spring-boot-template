package com.example.sbt.module.file;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private static final long DEFAULT_PRESIGNED_SECONDS = 24L * 60L * 60L;

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String uploadFile(byte[] file, String filePath) {
        try {
            if (StringUtils.isBlank(filePath)) return null;
            filePath = ConversionUtils.safeTrim(StringUtils.strip(filePath
                    .replaceAll("\\s*/\\s*", "/")
                    .replaceAll("/+", "/"), "/"));
            if (StringUtils.isBlank(filePath)) return null;
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(applicationProperties.getAwsS3Bucket())
                            .key(filePath)
                            .build(),
                    RequestBody.fromBytes(file));
            return filePath;
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public String createPresignedGetUrl(String filePath, Long seconds) {
        try {
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
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(applicationProperties.getAwsS3Bucket())
                            .key(filePath)
                            .build(),
                    ResponseTransformer.toBytes()
            );
            return objectBytes.asByteArray();
        } catch (Exception e) {
            log.error("getFile ", e);
            return null;
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            if (StringUtils.isBlank(filePath)) return;
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(applicationProperties.getAwsS3Bucket())
                            .key(filePath)
                            .build());
        } catch (Exception e) {
            log.error("deleteFile ", e);
        }
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        try {
            if (CollectionUtils.isEmpty(filePaths)) return;
            filePaths = filePaths.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filePaths)) return;
            List<ObjectIdentifier> objectIds = filePaths.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .collect(Collectors.toList());
            s3Client.deleteObjects(
                    DeleteObjectsRequest.builder()
                            .bucket(applicationProperties.getAwsS3Bucket())
                            .delete(Delete.builder().objects(objectIds).build())
                            .build());
        } catch (Exception e) {
            log.error("deleteFiles ", e);
        }
    }

}
