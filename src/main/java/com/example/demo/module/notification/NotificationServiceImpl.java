package com.example.demo.module.notification;

import com.example.demo.config.RequestContextHolder;
import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.Env;
import com.example.demo.common.constant.RedisKey;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.module.notification.dto.NotificationDTO;
import com.example.demo.module.notification.dto.SendNotificationStreamRequest;
import com.example.demo.common.util.ConversionUtils;
import com.example.demo.common.util.I18nHelper;
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
public class NotificationServiceImpl implements NotificationService {

    private final Env env;
    private final CommonMapper commonMapper;
    private final NotificationRepository notificationRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected NotificationDTO save(NotificationDTO notificationDTO) {
        return commonMapper.toDTO(notificationRepository.save(commonMapper.toEntity(notificationDTO)));
    }

    @Override
    public NotificationDTO send(NotificationDTO notificationDTO) {
        notificationDTO.setId(null);
        notificationDTO.setStatus(CommonStatus.PENDING);
        NotificationDTO result = save(notificationDTO);

        streamSend(result.getId());

        return result;
    }

    @Override
    public void executeSend(UUID notificationId) {
        // TODO: send notification
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isEmpty()) return;
        Notification notification = notificationOptional.get();
        if (!CommonStatus.PENDING.equals(notification.getStatus())) return;
        notification.setStatus(CommonStatus.DONE);
        notificationRepository.save(notification);
        log.info("Notification ".concat(ConversionUtils.safeToString(notification.getId())).concat(" sent"));
    }

    private void streamSend(UUID notificationId) {
        SendNotificationStreamRequest request = SendNotificationStreamRequest.builder()
                .requestContext(RequestContextHolder.get())
                .notificationId(notificationId)
                .build();
        ObjectRecord<String, SendNotificationStreamRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(RedisKey.SEND_NOTIFICATION_STREAM);
        redisTemplate.opsForStream().add(data);
    }

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(I18nHelper.getMessage("notification.new_comer_title", env.getApplicationVersion()))
                .content(I18nHelper.getMessage("notification.new_comer_content"))
                .userId(userId)
                .status(CommonStatus.PENDING)
                .build();
        return send(notificationDTO);
    }

}
