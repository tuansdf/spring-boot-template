package com.example.sbt.module.notification;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.Env;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.event.publisher.SendNotificationEventPublisher;
import com.example.sbt.module.notification.dto.NotificationDTO;
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
public class NotificationServiceImpl implements NotificationService {

    private final Env env;
    private final CommonMapper commonMapper;
    private final NotificationRepository notificationRepository;
    private final SendNotificationEventPublisher sendNotificationEventPublisher;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected NotificationDTO save(NotificationDTO notificationDTO) {
        return commonMapper.toDTO(notificationRepository.save(commonMapper.toEntity(notificationDTO)));
    }

    @Override
    public NotificationDTO triggerSend(NotificationDTO notificationDTO) {
        notificationDTO.setId(null);
        notificationDTO.setStatus(CommonStatus.PENDING);
        NotificationDTO result = save(notificationDTO);

        sendNotificationEventPublisher.publish(result.getId());

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

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(LocaleHelper.getMessage("notification.new_comer_title", env.getApplicationVersion()))
                .content(LocaleHelper.getMessage("notification.new_comer_content"))
                .userId(userId)
                .status(CommonStatus.PENDING)
                .build();
        return triggerSend(notificationDTO);
    }

}
