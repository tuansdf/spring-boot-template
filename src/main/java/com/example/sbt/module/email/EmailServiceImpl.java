package com.example.sbt.module.email;

import com.example.sbt.common.constant.*;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.event.publisher.SendEmailEventPublisher;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsDTO;
import com.example.sbt.module.email.dto.SearchEmailRequestDTO;
import com.example.sbt.module.configuration.ConfigurationService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EntityManager entityManager;

    private PaginationData<EmailDTO> executeSearch(SearchEmailRequestDTO requestDTO, boolean isCount) {
        PaginationData<EmailDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select e.* ");
        }
        builder.append(" from email e ");
        builder.append(" where 1=1 ");
        if (requestDTO.getUserId() != null) {
            builder.append(" and e.user_id = :userId ");
            params.put("userId", requestDTO.getUserId());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
            builder.append(" and e.status = :status ");
            params.put("status", requestDTO.getStatus().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and e.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and e.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by e.created_at desc, e.id asc ");
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.EMAIL_SEARCH);
            SQLHelper.setParams(query, params);
            List<EmailDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

    @Override
    public PaginationData<EmailDTO> search(SearchEmailRequestDTO requestDTO, boolean isCount) {
        PaginationData<EmailDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    @Override
    public EmailStatsDTO getStatsByUser(UUID userId) {
        EmailStatsDTO result = new EmailStatsDTO();
        result.setTotalRead(executeSearch(SearchEmailRequestDTO.builder().userId(userId).status(CommonStatus.READ).build(), true).getTotalItems());
        result.setTotalUnread(executeSearch(SearchEmailRequestDTO.builder().userId(userId).status(CommonStatus.UNREAD).build(), true).getTotalItems());
        return result;
    }

    @Override
    public EmailDTO findOneById(UUID id) {
        if (id == null) return null;
        Email result = emailRepository.findById(id).orElse(null);
        if (result == null || result.getUserId() == null || result.getUserId().equals(RequestContext.get().getUserId())) {
            return null;
        }
        if (CommonStatus.UNREAD.equals(result.getStatus())) {
            result.setStatus(CommonStatus.READ);
            result = emailRepository.save(result);
        }
        return commonMapper.toDTO(result);
    }

    @Override
    public EmailDTO triggerSend(EmailDTO emailDTO) {
        emailDTO.setId(null);
        emailDTO.setStatus(CommonStatus.UNREAD);
        emailDTO.setSendStatus(CommonStatus.PENDING);
        EmailDTO result = commonMapper.toDTO(emailRepository.save(commonMapper.toEntity(emailDTO)));

        sendEmailEventPublisher.publish(result);

        return result;
    }

    @Override
    public void executeSend(EmailDTO email) throws MessagingException {
        if (email == null || !CommonStatus.PENDING.equals(email.getSendStatus())) return;
        sendEmailService.send(commonMapper.toSendRequest(email));
        email.setStatus(CommonStatus.SENT);
        emailRepository.save(commonMapper.toEntity(email));
        log.info("Email {} sent", ConversionUtils.safeToString(email.getId()));
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
        String url = configurationService.findValueByCode(ConfigurationCode.ACTIVATE_ACCOUNT_URL);
        if (StringUtils.isBlank(url)) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        url = url.replace("{{{token}}}", token);
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
