package org.tuanna.xcloneserver.modules.email;

import org.tuanna.xcloneserver.modules.email.dtos.EmailDTO;

public interface EmailService {

    EmailDTO send(EmailDTO emailDTO);

}
