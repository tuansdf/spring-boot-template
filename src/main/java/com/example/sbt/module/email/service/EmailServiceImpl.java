package com.example.sbt.module.email.service;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.DateUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.infrastructure.helper.SQLHelper;
import com.example.sbt.module.authtoken.entity.AuthToken;
import com.example.sbt.module.configuration.service.Configurations;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsResponse;
import com.example.sbt.module.email.dto.SearchEmailRequest;
import com.example.sbt.module.email.entity.Email;
import com.example.sbt.module.email.event.SendEmailEventPublisher;
import com.example.sbt.module.email.repository.EmailRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class EmailServiceImpl implements EmailService {
    private final SQLHelper sqlHelper;
    private final LocaleHelper localeHelper;
    private final CommonMapper commonMapper;
    private final EmailRepository emailRepository;
    private final SendEmailEventPublisher sendEmailEventPublisher;
    private final SendEmailService sendEmailService;
    private final Configurations configurations;
    private final EntityManager entityManager;

    private PaginationData<EmailDTO> executeSearch(SearchEmailRequest requestDTO, boolean isCount) {
        PaginationData<EmailDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select ");
            builder.append(" e.id, e.user_id, e.to_email, e.cc_email, e.subject, e.body, e.is_html, ");
            builder.append(" e.retry_count, e.type, e.status, e.send_status, e.created_at, e.updated_at ");
        }
        builder.append(" from email e ");
        builder.append(" where 1=1 ");
        if (requestDTO.getUserId() != null) {
            builder.append(" and e.user_id = ? ");
            params.add(requestDTO.getUserId());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
            builder.append(" and e.status = ? ");
            params.add(requestDTO.getStatus().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and e.created_at >= ? ");
            params.add(requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and e.created_at < ? ");
            params.add(requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by e.id desc ");
            builder.append(" limit ? offset ? ");
            sqlHelper.setLimitOffset(params, result.getPageNumber(), result.getPageSize());
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(sqlHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            List<Object[]> objects = query.getResultList();
            List<EmailDTO> items = objects.stream().map(x -> {
                EmailDTO dto = new EmailDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setUserId(ConversionUtils.toUUID(x[1]));
                dto.setToEmail(ConversionUtils.toString(x[2]));
                dto.setCcEmail(ConversionUtils.toString(x[3]));
                dto.setSubject(ConversionUtils.toString(x[4]));
                dto.setBody(ConversionUtils.toString(x[5]));
                dto.setIsHtml(ConversionUtils.toBoolean(x[6]));
                dto.setRetryCount(ConversionUtils.toInteger(x[7]));
                dto.setType(ConversionUtils.toString(x[8]));
                dto.setStatus(ConversionUtils.toString(x[9]));
                dto.setSendStatus(ConversionUtils.toString(x[10]));
                dto.setCreatedAt(DateUtils.toInstant(x[11]));
                dto.setUpdatedAt(DateUtils.toInstant(x[12]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }

    private long executeSearchCount(SearchEmailRequest requestDTO) {
        return executeSearch(requestDTO, true).getTotalItems();
    }

    private List<EmailDTO> executeSearchList(SearchEmailRequest requestDTO) {
        return ConversionUtils.safeToList(executeSearch(requestDTO, false).getItems());
    }

    @Override
    public PaginationData<EmailDTO> search(SearchEmailRequest requestDTO, boolean isCount) {
        PaginationData<EmailDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearchList(requestDTO));
        }
        return result;
    }

    @Override
    public EmailStatsResponse getStatsByUser(UUID userId) {
        EmailStatsResponse result = new EmailStatsResponse();
        result.setTotalRead(executeSearchCount(SearchEmailRequest.builder().userId(userId).status(CommonStatus.READ).build()));
        result.setTotalUnread(executeSearchCount(SearchEmailRequest.builder().userId(userId).status(CommonStatus.UNREAD).build()));
        return result;
    }

    @Override
    public EmailDTO findOneById(UUID id, RequestContext requestContext) {
        if (id == null) return null;
        Email result = emailRepository.findTopByIdAndUserId(id, requestContext.getUserId()).orElse(null);
        if (result == null) {
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
    public void executeSend(EmailDTO email) {
        if (email == null || !CommonStatus.PENDING.equals(email.getSendStatus())) return;
        sendEmailService.send(commonMapper.toSendRequest(email));
        email.setStatus(CommonStatus.SENT);
        emailRepository.save(commonMapper.toEntity(email));
        log.info("Email {} sent", ConversionUtils.safeToString(email.getId()));
    }

    private void throttleSend(UUID userId, String type, Runnable onError) {
        Integer timeWindow = configurations.getEmailThrottleTimeWindow();
        if (timeWindow == null) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ConversionUtils.safeToBoolean(emailRepository.existsByUserIdAndTypeAndCreatedAtAfter(userId, type, Instant.now().minusSeconds(timeWindow)))) {
            if (onError != null) {
                onError.run();
            } else {
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public EmailDTO sendResetPasswordEmail(String email, String name, String token, UUID userId) {
        throttleSend(userId, AuthToken.Type.RESET_PASSWORD.toString(), () -> {
            throw new CustomException(localeHelper.getMessage("auth.reset_password_email_sent"), HttpStatus.OK);
        });
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUserId(userId);
        emailDTO.setToEmail(email);
        emailDTO.setSubject(localeHelper.getMessage("email.reset_password_subject"));
        emailDTO.setBody(localeHelper.getMessage("email.reset_password_content", name, token));
        emailDTO.setType(AuthToken.Type.RESET_PASSWORD.toString());
        return triggerSend(emailDTO);
    }

    @Override
    public EmailDTO sendActivateAccountEmail(String email, String name, String token, UUID userId) {
        throttleSend(userId, AuthToken.Type.ACTIVATE_ACCOUNT.toString(), () -> {
            throw new CustomException(localeHelper.getMessage("auth.activate_account_email_sent"), HttpStatus.OK);
        });
        String url = configurations.getActivateAccountUrl();
        if (StringUtils.isBlank(url)) {
            log.info("Activate account url is empty");
            return null;
        }
        url = url.replace("{{{token}}}", token);
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUserId(userId);
        emailDTO.setToEmail(email);
        emailDTO.setSubject(localeHelper.getMessage("email.activate_account_subject"));
        emailDTO.setBody(localeHelper.getMessage("email.activate_account_content", name, url));
        emailDTO.setType(AuthToken.Type.ACTIVATE_ACCOUNT.toString());
        return triggerSend(emailDTO);
    }
}
