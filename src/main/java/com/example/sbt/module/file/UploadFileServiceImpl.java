package com.example.sbt.module.file;

import com.example.sbt.common.constant.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    private final ApplicationProperties applicationProperties;
    private final S3Client s3Client;

    @Override
    public String upload(String filePath, byte[] file) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        if (filePath.startsWith("/")) {
            filePath = StringUtils.strip(filePath, "/");
        }
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(applicationProperties.getAwsS3Bucket())
                            .key(filePath)
                            .build(),
                    RequestBody.fromBytes(file));
            URL url = getFileUrl(filePath);
            if (url == null) return null;
            return url.toString();
        } catch (Exception e) {
            log.error("uploadFile ", e);
            return null;
        }
    }

    @Override
    public URL getFileUrl(String filePath) {
        try {
            return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(applicationProperties.getAwsS3Bucket()).key(filePath).build());
        } catch (Exception e) {
            return null;
        }
    }

}
