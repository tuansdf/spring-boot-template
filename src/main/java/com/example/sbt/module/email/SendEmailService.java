package com.example.sbt.module.email;

import com.example.sbt.module.email.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface SendEmailService {

    void send(SendEmailRequest request) throws MessagingException;

    @Async
    void sendAsync(SendEmailRequest request) throws MessagingException;

}
