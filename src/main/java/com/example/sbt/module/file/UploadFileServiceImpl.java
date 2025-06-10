package com.example.sbt.module.file;

import com.example.sbt.common.constant.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;

    @Override
    public String upload(byte[] file, String filePath) {
        if (StringUtils.isBlank(filePath)) return null;
        filePath = StringUtils.strip(filePath, "/");
        if (StringUtils.isBlank(filePath)) return null;
        try {
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

}
