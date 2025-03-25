package com.example.sbt.module.notification;

import com.example.sbt.module.notification.dto.NotificationDTO;

import java.util.UUID;

public interface NotificationService {

    NotificationDTO triggerSend(NotificationDTO notificationDTO);

    void executeSend(UUID notificationId);

    NotificationDTO sendNewComerNotification(UUID userId);
}
