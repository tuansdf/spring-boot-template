package com.example.springboot.modules.email;

import com.example.springboot.modules.email.dtos.EmailDTO;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

    EmailDTO sendResetPasswordEmail(String email, String name, String token);

    EmailDTO sendActivateAccountEmail(String email, String name, String token);
}
