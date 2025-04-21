package com.example.sbt.module.email;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.CommonType;
import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.event.publisher.SendEmailEventPublisher;
import com.example.sbt.module.configuration.ConfigurationService;
import com.example.sbt.module.email.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class EmailServiceImpl implements EmailService {

    private final ApplicationProperties applicationProperties;
    private final CommonMapper commonMapper;
    private final EmailRepository emailRepository;
    private final SendEmailEventPublisher sendEmailEventPublisher;
    private final SendEmailService sendEmailService;
    private final ConfigurationService configurationService;

    protected EmailDTO save(EmailDTO emailDTO) {
        return commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
    }

    @Override
    public EmailDTO triggerSend(EmailDTO emailDTO) {
        emailDTO.setId(null);
        emailDTO.setStatus(CommonStatus.PENDING);
        EmailDTO result = save(emailDTO);

        sendEmailEventPublisher.publish(result);

        return result;
    }

    @Override
    public void executeSend(EmailDTO email) throws MessagingException {
        try {
            if (email == null || !CommonStatus.PENDING.equals(email.getStatus())) return;
            sendEmailService.send(commonMapper.toSendEmailRequest(email));
            email.setStatus(CommonStatus.DONE);
            emailRepository.save(commonMapper.toEntity(email));
            log.info("Email ".concat(ConversionUtils.safeToString(email.getId())).concat(" sent"));
        } catch (Exception e) {
            log.error("executeSend", e);
            throw e;
        }
    }

    private void throttleSend(UUID userId, String type, String messageKey) {
        Integer timeWindow = applicationProperties.getEmailThrottleTimeWindow();
        if (timeWindow == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ConversionUtils.safeToBoolean(emailRepository.existsRecentByUserIdAndType(userId, type, Instant.now().minusSeconds(timeWindow)))) {
            throw new CustomException(LocaleHelper.getMessage(messageKey), HttpStatus.OK);
        }
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId) {
        throttleSend(userId, CommonType.RESET_PASSWORD, "auth.reset_password_email_sent");
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .toEmail(email)
                .subject(LocaleHelper.getMessage("email.reset_password_subject"))
                .body(LocaleHelper.getMessage("email.reset_password_content", name, token))
                .type(CommonType.RESET_PASSWORD)
                .build();
        return triggerSend(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId) {
        throttleSend(userId, CommonType.ACTIVATE_ACCOUNT, "auth.activate_account_email_sent");
        String url = applicationProperties.getServerBaseUrl().concat("/pub/auth/account/activate?token=").concat(token);
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .toEmail(email)
                .subject(LocaleHelper.getMessage("email.activate_account_subject"))
                .body(LocaleHelper.getMessage("email.activate_account_content", name, url))
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return triggerSend(emailDTO);
    }

}
