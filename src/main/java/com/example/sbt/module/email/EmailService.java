package com.example.sbt.module.email;

import com.example.sbt.module.email.dto.EmailDTO;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface EmailService {

    EmailDTO triggerSend(EmailDTO emailDTO);

    void executeSend(EmailDTO emailDTO) throws MessagingException;

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);

}
