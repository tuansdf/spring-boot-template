package com.example.sbt.module.email.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsDTO;
import com.example.sbt.module.email.dto.SearchEmailRequestDTO;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface EmailService {
    PaginationData<EmailDTO> search(SearchEmailRequestDTO requestDTO, boolean isCount);

    EmailStatsDTO getStatsByUser(UUID userId);

    EmailDTO findOneById(UUID id);

    EmailDTO triggerSend(EmailDTO emailDTO);

    void executeSend(EmailDTO emailDTO);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);
}
