package com.example.demo.modules.notification;

import com.example.demo.modules.notification.dtos.NotificationDTO;

import java.util.UUID;

public interface NotificationService {

    NotificationDTO send(NotificationDTO notificationDTO);

    void executeSend(UUID notificationId);

    NotificationDTO sendNewComerNotification(UUID actionBy);
}
