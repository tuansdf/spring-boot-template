package com.example.demo.module.notification;

import com.example.demo.module.notification.dto.NotificationDTO;

import java.util.UUID;

public interface NotificationService {

    NotificationDTO triggerSend(NotificationDTO notificationDTO);

    void executeSend(UUID notificationId);

    NotificationDTO sendNewComerNotification(UUID userId);
}
