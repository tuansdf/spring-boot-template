package com.example.sbt.features.notification.service;

import com.example.sbt.common.constant.CustomProperties;
import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.DateUtils;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.infrastructure.helper.SQLHelper;
import com.example.sbt.features.notification.dto.NotificationDTO;
import com.example.sbt.features.notification.dto.NotificationStatsResponse;
import com.example.sbt.features.notification.dto.SearchNotificationRequest;
import com.example.sbt.features.notification.dto.SendNotificationRequest;
import com.example.sbt.features.notification.entity.Notification;
import com.example.sbt.features.notification.event.SendNotificationEventPublisher;
import com.example.sbt.features.notification.repository.NotificationRepository;
import com.example.sbt.features.userdevice.service.UserDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class NotificationServiceImpl implements NotificationService {
    private final SQLHelper sqlHelper;
    private final LocaleHelper localeHelper;
    private final CustomProperties customProperties;
    private final CommonMapper commonMapper;
    private final NotificationRepository notificationRepository;
    private final SendNotificationEventPublisher sendNotificationEventPublisher;
    private final SendNotificationService sendNotificationService;
    private final UserDeviceService userDeviceService;
    private final EntityManager entityManager;

    private PaginationData<NotificationDTO> executeSearch(SearchNotificationRequest requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = sqlHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
        List<Object> params = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (isCount) {
            builder.append(" select count(*) ");
        } else {
            builder.append(" select ");
            builder.append(" n.id, n.user_id, n.title, n.body, n.data, n.topic, n.retry_count, ");
            builder.append(" n.type, n.status, n.send_status, n.created_at, n.updated_at ");
        }
        builder.append(" from notification n ");
        builder.append(" where 1=1 ");
        if (requestDTO.getUserId() != null) {
            builder.append(" and n.user_id = ? ");
            params.add(requestDTO.getUserId());
        }
        if (StringUtils.isNotBlank(requestDTO.getStatus())) {
            builder.append(" and n.status = ? ");
            params.add(requestDTO.getStatus().trim());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and n.created_at >= ? ");
            params.add(requestDTO.getCreatedAtFrom());
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and n.created_at < ? ");
            params.add(requestDTO.getCreatedAtTo());
        }
        if (!isCount) {
            builder.append(" order by n.id desc ");
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
            List<NotificationDTO> items = objects.stream().map(x -> {
                NotificationDTO dto = new NotificationDTO();
                dto.setId(ConversionUtils.toUUID(x[0]));
                dto.setUserId(ConversionUtils.toUUID(x[1]));
                dto.setTitle(ConversionUtils.toString(x[2]));
                dto.setBody(ConversionUtils.toString(x[3]));
                dto.setData(ConversionUtils.toString(x[4]));
                dto.setTopic(ConversionUtils.toString(x[5]));
                dto.setRetryCount(ConversionUtils.toInteger(x[6]));
                dto.setType(ConversionUtils.toString(x[7]));
                dto.setStatus(ConversionUtils.toString(x[8]));
                dto.setSendStatus(ConversionUtils.toString(x[9]));
                dto.setCreatedAt(DateUtils.toInstant(x[10]));
                dto.setUpdatedAt(DateUtils.toInstant(x[11]));
                return dto;
            }).collect(Collectors.toCollection(ArrayList::new));
            result.setItems(items);
        }
        return result;
    }

    private long executeSearchCount(SearchNotificationRequest requestDTO) {
        return executeSearch(requestDTO, true).getTotalItems();
    }

    private List<NotificationDTO> executeSearchList(SearchNotificationRequest requestDTO) {
        return ConversionUtils.safeToList(executeSearch(requestDTO, false).getItems());
    }

    @Override
    public PaginationData<NotificationDTO> search(SearchNotificationRequest requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearchList(requestDTO));
        }
        return result;
    }

    @Override
    public NotificationStatsResponse getStatsByUser(UUID userId) {
        NotificationStatsResponse result = new NotificationStatsResponse();
        result.setTotalRead(executeSearchCount(SearchNotificationRequest.builder().userId(userId).status(CommonStatus.READ).build()));
        result.setTotalUnread(executeSearchCount(SearchNotificationRequest.builder().userId(userId).status(CommonStatus.UNREAD).build()));
        return result;
    }

    @Override
    public NotificationDTO findOneById(UUID id) {
        if (id == null) return null;
        Notification result = notificationRepository.findTopByIdAndUserId(id, RequestContextHolder.get().getUserId()).orElse(null);
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
        sendNotificationEventPublisher.publish(result);
        return result;
    }

    @Override
    public void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException {
        if (notificationDTO == null || !CommonStatus.PENDING.equals(notificationDTO.getSendStatus())) return;
        Set<String> tokens = userDeviceService.findAllTokensByUserId(notificationDTO.getUserId());
        SendNotificationRequest request = commonMapper.toSendRequest(notificationDTO);
        request.setTokens(tokens);
        sendNotificationService.send(request);
        notificationDTO.setSendStatus(CommonStatus.SENT);
        notificationRepository.save(commonMapper.toEntity(notificationDTO));
        log.info("Notification {} sent", ConversionUtils.safeToString(notificationDTO.getId()));
    }

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle(localeHelper.getMessage("notification.new_comer_title", customProperties.getAppName()));
        notificationDTO.setBody(localeHelper.getMessage("notification.new_comer_content"));
        notificationDTO.setUserId(userId);
        return triggerSend(notificationDTO);
    }
}
