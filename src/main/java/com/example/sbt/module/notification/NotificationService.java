package com.example.sbt.module.notification;

import com.example.sbt.module.notification.dto.NotificationDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.UUID;

public interface NotificationService {

    NotificationDTO triggerSend(NotificationDTO notificationDTO);

    void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException;

    NotificationDTO sendNewComerNotification(UUID userId);

}
