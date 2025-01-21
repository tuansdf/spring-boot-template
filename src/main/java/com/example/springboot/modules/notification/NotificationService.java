package com.example.springboot.modules.notification;

import com.example.springboot.modules.notification.dtos.NotificationDTO;

import java.util.UUID;

public interface NotificationService {

    NotificationDTO send(NotificationDTO notificationDTO);

    void executeSend(UUID notificationId);

    NotificationDTO sendNewComerNotification(UUID actionBy);
}
