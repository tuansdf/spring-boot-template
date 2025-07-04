package com.example.sbt.config;

import com.example.sbt.core.constant.ApplicationProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FirebaseConfig {
    private final ApplicationProperties applicationProperties;

    @Bean
    public GoogleCredentials googleCredentials() {
        try {
            byte[] firebaseServiceAccount = Base64.decodeBase64(applicationProperties.getFirebaseServiceAccount());
            if (firebaseServiceAccount == null) {
                log.error("Missing Firebase service account credentials");
                return null;
            }
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(firebaseServiceAccount)) {
                return GoogleCredentials.fromStream(inputStream);
            }
        } catch (Exception e) {
            log.error("GoogleCredentials ", e);
            return null;
        }
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials) {
        if (credentials == null) return null;
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        if (firebaseApp == null) return null;
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
