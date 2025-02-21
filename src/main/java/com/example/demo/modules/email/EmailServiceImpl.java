package com.example.demo.modules.email;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.constants.CommonStatus;
import com.example.demo.constants.CommonType;
import com.example.demo.constants.Env;
import com.example.demo.constants.RedisKey;
import com.example.demo.entities.Email;
import com.example.demo.mappers.CommonMapper;
import com.example.demo.modules.email.dtos.EmailDTO;
import com.example.demo.modules.email.dtos.SendEmailStreamRequest;
import com.example.demo.utils.ConversionUtils;
import com.example.demo.utils.I18nHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected EmailDTO save(EmailDTO emailDTO) {
        return commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));
    }

    @Override
    public EmailDTO send(EmailDTO emailDTO) {
        emailDTO.setId(null);
        emailDTO.setStatus(CommonStatus.PENDING);
        EmailDTO result = save(emailDTO);

        streamSend(result.getId());

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

    private void streamSend(UUID emailId) {
        SendEmailStreamRequest request = SendEmailStreamRequest.builder()
                .requestContext(RequestContextHolder.get())
                .emailId(emailId)
                .build();
        ObjectRecord<String, SendEmailStreamRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(RedisKey.SEND_EMAIL_STREAM);
        redisTemplate.opsForStream().add(data);
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID actionBy) {
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.reset_password_subject"))
                .content(I18nHelper.getMessage("email.reset_password_content", name, token))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .type(CommonType.RESET_PASSWORD)
                .build();
        return send(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID actionBy) {
        EmailDTO emailDTO = EmailDTO.builder()
                .fromEmail(env.getFromEmail())
                .toEmail(email)
                .subject(I18nHelper.getMessage("email.activate_account_subject"))
                .content(I18nHelper.getMessage("email.activate_account_content", name, token))
                .createdBy(actionBy)
                .updatedBy(actionBy)
                .type(CommonType.ACTIVATE_ACCOUNT)
                .build();
        return send(emailDTO);
    }

}
