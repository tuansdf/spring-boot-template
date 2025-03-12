package com.example.demo.module.email;

import com.example.demo.module.email.dto.EmailDTO;

import java.util.UUID;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

    void executeSend(UUID emailId);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);

}
