package com.example.demo.module.email;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.CommonType;
import com.example.demo.common.constant.Env;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.ConversionUtils;
import com.example.demo.common.util.I18nHelper;
import com.example.demo.event.SendEmailEventPublisher;
import com.example.demo.module.email.dto.EmailDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    protected EmailDTO save(EmailDTO emailDTO) {
        return commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
    }

    @Override
    public EmailDTO send(EmailDTO emailDTO) {
        emailDTO.setId(null);
        emailDTO.setStatus(CommonStatus.PENDING);
        EmailDTO result = save(emailDTO);

        sendEmailEventPublisher.publish(result.getId());

        return result;
    }


    @Override
    public void executeSend(UUID emailId) {
        // TODO: send email
        Optional<Email> emailOptional = emailRepository.findById(emailId);
        if (emailOptional.isEmpty()) return;
        Email email = emailOptional.get();
        if (!CommonStatus.PENDING.equals(email.getStatus())) return;
        email.setStatus(CommonStatus.DONE);
        emailRepository.save(email);
        log.info("Email ".concat(ConversionUtils.safeToString(email.getId())).concat(" sent"));
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId) {
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.reset_password_subject"))
                .content(I18nHelper.getMessage("email.reset_password_content", name, token))
                .type(CommonType.RESET_PASSWORD)
                .build();
        return send(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId) {
        EmailDTO emailDTO = EmailDTO.builder()
                .userId(userId)
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.activate_account_subject"))
                .content(I18nHelper.getMessage("email.activate_account_content", name, token))
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return send(emailDTO);
    }

}
