package org.tuanna.xcloneserver.modules.email;

import org.tuanna.xcloneserver.modules.email.dtos.EmailDTO;

import java.util.Locale;
import java.util.UUID;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID actionBy, Locale locale);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID actionBy, Locale locale);
}
