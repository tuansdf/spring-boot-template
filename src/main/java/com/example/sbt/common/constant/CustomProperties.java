package com.example.sbt.common.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom", ignoreInvalidFields = false, ignoreUnknownFields = true)
public class CustomProperties {
    private String appName;
    private String appVersion;

    private String jwtAccessSecret;
    private String jwtRefreshSecret;
    private String jwtOauth2Secret;
    private String jwtPasswordResetSecret;
    private String jwtAccountActivationSecret;
    private Integer jwtAccessLifetime;
    private Integer jwtRefreshLifetime;
    private Integer jwtOauth2Lifetime;
    private Integer jwtPasswordResetLifetime;
    private Integer jwtAccountActivationLifetime;

    private String serverBaseUrl;
    private String clientBaseUrl;

    private String firebaseServiceAccount;
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;
    private String awsS3Bucket;

    private String mailFrom;
    private String mailFromName;

    private String oauth2CallbackUrl;
}
