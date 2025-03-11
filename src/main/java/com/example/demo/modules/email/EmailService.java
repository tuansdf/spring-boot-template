package com.example.demo.modules.email;

import com.example.demo.modules.email.dtos.EmailDTO;

import java.util.UUID;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

    void executeSend(UUID emailId);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);

}
