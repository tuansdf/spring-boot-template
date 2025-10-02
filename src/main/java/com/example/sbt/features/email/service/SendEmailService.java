package com.example.sbt.features.email.service;

import com.example.sbt.features.email.dto.SendEmailRequest;
import org.springframework.scheduling.annotation.Async;

public interface SendEmailService {
    void send(SendEmailRequest request);

    @Async
    void sendAsync(SendEmailRequest request);
}
