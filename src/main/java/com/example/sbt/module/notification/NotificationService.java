package com.example.sbt.module.notification;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.notification.dto.SearchNotificationRequestDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.UUID;

public interface NotificationService {

    PaginationData<NotificationDTO> search(SearchNotificationRequestDTO requestDTO, boolean isCount);

    NotificationDTO triggerSend(NotificationDTO notificationDTO);

    void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException;

    NotificationDTO sendNewComerNotification(UUID userId);

}
