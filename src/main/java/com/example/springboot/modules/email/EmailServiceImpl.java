package com.example.springboot.modules.email;

import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.CommonType;
import com.example.springboot.constants.Env;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.email.dtos.EmailDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class EmailServiceImpl implements EmailService {

    private final Env env;
    private final CommonMapper commonMapper;
    private final MessageSource messageSource;
    private final EmailRepository emailRepository;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Override
    public EmailDTO send(EmailDTO emailDTO) {
        emailDTO.setStatus(CommonStatus.PENDING);
        EmailDTO result = commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
//        ExecuteSendEmailStreamRequest request = ExecuteSendEmailStreamRequest.builder()
//                        .requestContext(RequestContextHolder.get())
//        ExecuteSendEmailStreamListener.add(redisTemplate, result);
        return result;
    }

    @Override
    public void executeSend(UUID emailId) {
        // TODO: send email
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID actionBy, Locale locale) {
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(messageSource.getMessage("email.reset_password_subject", null, locale))
                .content(messageSource.getMessage("email.reset_password_content", new String[]{name, token}, locale))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .type(CommonType.RESET_PASSWORD)
                .build();
        return send(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID actionBy, Locale locale) {
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(messageSource.getMessage("email.activate_account_subject", null, locale))
                .content(messageSource.getMessage("email.activate_account_content", new String[]{name, token}, locale))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return send(emailDTO);
    }

}
