package com.example.sbt.module.notification;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.SQLHelper;
import com.example.sbt.event.publisher.SendNotificationEventPublisher;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.notification.dto.SearchNotificationRequestDTO;
import com.example.sbt.module.userdevice.UserDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
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

    private final ApplicationProperties applicationProperties;
    private final CommonMapper commonMapper;
    private final NotificationRepository notificationRepository;
    private final SendNotificationEventPublisher sendNotificationEventPublisher;
    private final SendNotificationService sendNotificationService;
    private final UserDeviceService userDeviceService;
    private final EntityManager entityManager;

    @Override
    public PaginationData<NotificationDTO> search(SearchNotificationRequestDTO requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = executeSearch(requestDTO, true);
        if (!isCount && result.getTotalItems() > 0) {
            result.setItems(executeSearch(requestDTO, false).getItems());
        }
        return result;
    }

    private PaginationData<NotificationDTO> executeSearch(SearchNotificationRequestDTO requestDTO, boolean isCount) {
        PaginationData<NotificationDTO> result = SQLHelper.initData(requestDTO.getPageNumber(), requestDTO.getPageSize());
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
        if (StringUtils.isNotEmpty(requestDTO.getStatus())) {
            builder.append(" and n.status = :status ");
            params.put("status", requestDTO.getStatus());
        }
        if (requestDTO.getCreatedAtFrom() != null) {
            builder.append(" and n.created_at >= :createdAtFrom ");
            params.put("createdAtFrom", requestDTO.getCreatedAtFrom().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (requestDTO.getCreatedAtTo() != null) {
            builder.append(" and n.created_at <= :createdAtTo ");
            params.put("createdAtTo", requestDTO.getCreatedAtTo().truncatedTo(SQLHelper.MIN_TIME_PRECISION));
        }
        if (!isCount) {
            builder.append(SQLHelper.toLimitOffset(result.getPageNumber(), result.getPageSize()));
        }
        if (isCount) {
            Query query = entityManager.createNativeQuery(builder.toString());
            SQLHelper.setParams(query, params);
            long count = ConversionUtils.safeToLong(query.getSingleResult());
            result.setTotalItems(count);
            result.setTotalPages(SQLHelper.toPages(count, result.getPageSize()));
        } else {
            Query query = entityManager.createNativeQuery(builder.toString(), ResultSetName.NOTIFICATION_SEARCH);
            SQLHelper.setParams(query, params);
            List<NotificationDTO> items = query.getResultList();
            result.setItems(items);
        }
        return result;
    }

    protected NotificationDTO save(NotificationDTO notificationDTO) {
        return commonMapper.toDTO(notificationRepository.save(commonMapper.toEntity(notificationDTO)));
    }

    @Override
    public NotificationDTO triggerSend(NotificationDTO notificationDTO) {
        notificationDTO.setId(null);
        notificationDTO.setStatus(CommonStatus.PENDING);
        NotificationDTO result = save(notificationDTO);

        sendNotificationEventPublisher.publish(notificationDTO);

        return result;
    }

    @Override
    public void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException {
        if (notificationDTO == null || !CommonStatus.PENDING.equals(notificationDTO.getStatus())) return;
        List<String> tokens = userDeviceService.findAllTokensByUserId(notificationDTO.getUserId());
        for (String token : tokens) {
            sendNotificationService.send(Message.builder()
                    .setToken(token)
                    .putData("title", notificationDTO.getTitle())
                    .putData("body", notificationDTO.getContent())
                    .build());
        }
        notificationDTO.setStatus(CommonStatus.DONE);
        notificationRepository.save(commonMapper.toEntity(notificationDTO));
        log.info("Notification {} sent", ConversionUtils.safeToString(notificationDTO.getId()));
    }

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(LocaleHelper.getMessage("notification.new_comer_title", applicationProperties.getApplicationName()))
                .content(LocaleHelper.getMessage("notification.new_comer_content"))
                .userId(userId)
                .status(CommonStatus.PENDING)
                .build();
        return triggerSend(notificationDTO);
    }

}
