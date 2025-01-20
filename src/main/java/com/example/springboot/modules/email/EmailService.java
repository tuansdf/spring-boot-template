package com.example.springboot.modules.email;

import com.example.springboot.modules.email.dtos.EmailDTO;

import java.util.UUID;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

    void executeSend(UUID emailId);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID actionBy);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID actionBy);

}
