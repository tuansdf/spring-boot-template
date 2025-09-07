package com.example.sbt.core.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {
    @Value("${custom.app-name}")
    private String applicationName;
    @Value("${custom.app-version}")
    private String applicationVersion;

    @Value("${custom.jwt.secret}")
    private String jwtSecret;
    @Value("${custom.jwt.access-lifetime}")
    private Integer jwtAccessLifetime;
    @Value("${custom.jwt.refresh-lifetime}")
    private Integer jwtRefreshLifetime;
    @Value("${custom.jwt.reset-password-lifetime}")
    private Integer jwtResetPasswordLifetime;
    @Value("${custom.jwt.activate-account-lifetime}")
    private Integer jwtActivateAccountLifetime;

    @Value("${custom.server-base-url}")
    private String serverBaseUrl;
    @Value("${custom.client-base-url}")
    private String clientBaseUrl;

    @Value("${custom.firebase.service-account}")
    private String firebaseServiceAccount;
    @Value("${custom.aws.access-key}")
    private String awsAccessKey;
    @Value("${custom.aws.secret-key}")
    private String awsSecretKey;
    @Value("${custom.aws.region}")
    private String awsRegion;
    @Value("${custom.aws.s3-bucket}")
    private String awsS3Bucket;

    @Value("${custom.mail.from}")
    private String mailFrom;
    @Value("${custom.mail.from-name}")
    private String mailFromName;
}
