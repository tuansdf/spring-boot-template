package com.example.sbt.module.email.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsResponse;
import com.example.sbt.module.email.dto.SearchEmailRequest;

import java.util.UUID;

public interface EmailService {
    PaginationData<EmailDTO> search(SearchEmailRequest requestDTO, boolean isCount);

    EmailStatsResponse getStatsByUser(UUID userId);

    EmailDTO findOneById(UUID id, RequestContext requestContext);

    EmailDTO triggerSend(EmailDTO emailDTO);

    void executeSend(EmailDTO emailDTO);

    EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId);

    EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId);
}
