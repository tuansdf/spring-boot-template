package com.example.demo.module.email;

import com.example.demo.module.email.dto.EmailDTO;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface EmailService {

    EmailDTO startSend(EmailDTO emailDTO);

    void executeSend(EmailDTO emailDTO) throws MessagingException;

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);

}
