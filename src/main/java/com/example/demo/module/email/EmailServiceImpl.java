package com.example.demo.module.email;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.CommonType;
import com.example.demo.common.constant.Env;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.ConversionUtils;
import com.example.demo.common.util.I18nHelper;
import com.example.demo.event.publisher.SendEmailEventPublisher;
import com.example.demo.module.email.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class EmailServiceImpl implements EmailService {

    private final Env env;
    private final CommonMapper commonMapper;
    private final EmailRepository emailRepository;
    private final SendEmailEventPublisher sendEmailEventPublisher;
    private final SendEmailService sendEmailService;

    protected EmailDTO save(EmailDTO emailDTO) {
        return commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
    }

    public EmailDTO startSend(EmailDTO emailDTO) {
        emailDTO.setId(null);
        emailDTO.setStatus(CommonStatus.PENDING);
        EmailDTO result = save(emailDTO);

//        sendEmailEventPublisher.publish(result);
        try {
            executeSend(emailDTO);
        } catch (Exception ignore) {
        }

        return result;
    }

    @Override
    public void executeSend(EmailDTO email) throws MessagingException {
        try {
            if (!CommonStatus.PENDING.equals(email.getStatus())) return;
            sendEmailService.send(commonMapper.toSendEmailRequest(email));
            email.setStatus(CommonStatus.DONE);
            emailRepository.save(commonMapper.toEntity(email));
            log.info("Email ".concat(ConversionUtils.safeToString(email.getId())).concat(" sent"));
        } catch (Exception e) {
            log.error("executeSend", e);
            throw e;
        }
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId) {
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.reset_password_subject"))
                .body(I18nHelper.getMessage("email.reset_password_content", name, token))
                .type(CommonType.RESET_PASSWORD)
                .build();
        return startSend(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId) {
        String url = env.getServerBaseUrl().concat("/public/auth/account/activate?token=").concat(token);
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.activate_account_subject"))
                .body(I18nHelper.getMessage("email.activate_account_content", name, url))
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return startSend(emailDTO);
    }

}
