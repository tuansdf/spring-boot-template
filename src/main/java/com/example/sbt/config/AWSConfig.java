package com.example.sbt.config;

import com.example.sbt.common.constant.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@RequiredArgsConstructor
@Configuration
public class AWSConfig {

    private final ApplicationProperties applicationProperties;

    @Bean
    public AwsCredentials awsCredentials() {
        return AwsBasicCredentials.create(applicationProperties.getAwsAccessKey(), applicationProperties.getAwsSecretKey());
    }

    @Bean
    public S3Client s3Client(AwsCredentials awsCredentials) {
        return S3Client
                .builder()
                .region(Region.of(applicationProperties.getAwsRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentials awsCredentials) {
        return S3Presigner
                .builder()
                .region(Region.of(applicationProperties.getAwsRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
