package com.example.sbt.module.email;

import com.example.sbt.module.email.dto.SendEmailRequest;
import jakarta.mail.MessagingException;

public interface SendEmailService {

    void send(SendEmailRequest request) throws MessagingException;

}
