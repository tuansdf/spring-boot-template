package com.example.demo.module.email;

import com.example.demo.module.email.dto.SendEmailRequest;
import jakarta.mail.MessagingException;

public interface SendEmailService {

    void send(SendEmailRequest request) throws MessagingException;

}
