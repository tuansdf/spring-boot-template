package com.example.sbt.module.notification;

import com.example.sbt.common.constant.CommonStatus;
import com.example.sbt.common.constant.Env;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.event.publisher.SendNotificationEventPublisher;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.userdevice.UserDeviceService;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final SendNotificationService sendNotificationService;
    private final UserDeviceService userDeviceService;

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
        log.info("Notification ".concat(ConversionUtils.safeToString(notificationDTO.getId())).concat(" sent"));
    }

    @Override
    public NotificationDTO sendNewComerNotification(UUID userId) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .title(LocaleHelper.getMessage("notification.new_comer_title", env.getApplicationName()))
                .content(LocaleHelper.getMessage("notification.new_comer_content"))
                .userId(userId)
                .status(CommonStatus.PENDING)
                .build();
        return triggerSend(notificationDTO);
    }

}
