package com.example.sbt.module.notification.service;

import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.SQLHelper;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.event.publisher.SendNotificationEventPublisher;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.notification.dto.NotificationStatsDTO;
import com.example.sbt.module.notification.dto.SearchNotificationRequestDTO;
import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.example.sbt.module.notification.entity.Notification;
import com.example.sbt.module.notification.repository.NotificationRepository;
import com.example.sbt.module.userdevice.service.UserDeviceService;
import com.example.sbt.shared.util.ConversionUtils;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class NotificationServiceImpl implements NotificationService {
    private final SQLHelper sqlHelper;
    private final LocaleHelper localeHelper;
    private final ApplicationProperties applicationProperties;
    private final CommonMapper commonMapper;
    private final NotificationRepository notificationRepository;
    private final SendNotificationEventPublisher sendNotificationEventPublisher;
    private final SendNotificationService sendNotificationService;
    private final UserDeviceService userDeviceService;
    private final EntityManager entityManager;

    private PaginationData<NotificationDTO> executeSearch(SearchNotificationRequestDTO requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        Map<String, Object> params = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select n.* ");
        }
        builder.append(" from notification n ");
        builder.append(" where 1=1 ");
        if (requestDTO.getUserId() != null) {
            builder.append(" and n.user_id = :userId ");
            params.put("userId", requestDTO.getUserId());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
            builder.append(" and n.status = :status ");
            params.put("status", requestDTO.getStatus().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and n.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and n.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by n.created_at desc, n.id asc ");
            builder.append(sqlHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            sqlHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(sqlHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.NOTIFICATION_SEARCH);
            sqlHelper.setParams(query, params);
            List<NotificationDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

    @Override
    public PaginationData<NotificationDTO> search(SearchNotificationRequestDTO requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    @Override
    public NotificationStatsDTO getStatsByUser(UUID userId) {
        NotificationStatsDTO result = new NotificationStatsDTO();
        result.setTotalRead(executeSearch(SearchNotificationRequestDTO.builder().userId(userId).status(CommonStatus.READ).build(), true).getTotalItems());
        result.setTotalUnread(executeSearch(SearchNotificationRequestDTO.builder().userId(userId).status(CommonStatus.UNREAD).build(), true).getTotalItems());
        return result;
    }

    @Override
    public NotificationDTO findOneById(UUID id) {
        if (id == null) return null;
        Notification result = notificationRepository.findTopByIdAndUserId(id, RequestContext.get().getUserId()).orElse(null);
        if (result == null) {
            return null;
        }
        if (CommonStatus.UNREAD.equals(result.getStatus())) {
            result.setStatus(CommonStatus.READ);
            result = notificationRepository.save(result);
        }
        return commonMapper.toDTO(result);
    }

    @Override
    public NotificationDTO triggerSend(NotificationDTO notificationDTO) {
        notificationDTO.setId(null);
        notificationDTO.setStatus(CommonStatus.UNREAD);
        notificationDTO.setSendStatus(CommonStatus.PENDING);
        NotificationDTO result = commonMapper.toDTO(notificationRepository.save(commonMapper.toEntity(notificationDTO)));
        sendNotificationEventPublisher.publish(notificationDTO);
        return result;
    }

    @Override
    public void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException {
        if (notificationDTO == null || !CommonStatus.PENDING.equals(notificationDTO.getSendStatus())) return;
        List<String> tokens = userDeviceService.findAllTokensByUserId(notificationDTO.getUserId());
        SendNotificationRequest request = commonMapper.toSendRequest(notificationDTO);
        request.setTokens(tokens);
        sendNotificationService.send(request);
        notificationDTO.setSendStatus(CommonStatus.SENT);
        notificationRepository.save(commonMapper.toEntity(notificationDTO));
        log.info("Notification {} sent", ConversionUtils.safeToString(notificationDTO.getId()));
    }

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(localeHelper.getMessage("notification.new_comer_title", applicationProperties.getApplicationName()))
                .body(localeHelper.getMessage("notification.new_comer_content"))
                .userId(userId)
                .build();
        return triggerSend(notificationDTO);
    }
}
