package com.example.sbt.config;

import com.example.sbt.common.constant.Env;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Configuration
public class AWSConfig {

    private final Env env;

    @Bean
    public AwsCredentials awsCredentials() {
        return AwsBasicCredentials.create(env.getAwsAccessKey(), env.getAwsSecretKey());
    }

    @Bean
    public S3Client s3Client(AwsCredentials awsCredentials) {
        return S3Client
                .builder()
                .region(Region.of(env.getAwsRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
