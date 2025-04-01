package com.example.sbt.module.file;

import com.example.sbt.common.constant.Env;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final Env env;
    private final S3Client s3Client;

    @Override
    public String upload(String filePath, byte[] file) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(env.getAwsBucket())
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
            return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(env.getAwsBucket()).key(filePath).build());
        } catch (Exception e) {
            return null;
        }
    }

}
