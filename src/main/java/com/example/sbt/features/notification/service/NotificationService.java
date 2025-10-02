package com.example.sbt.features.notification.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.features.notification.dto.NotificationDTO;
import com.example.sbt.features.notification.dto.NotificationStatsResponse;
import com.example.sbt.features.notification.dto.SearchNotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.UUID;

public interface NotificationService {
    PaginationData<NotificationDTO> search(SearchNotificationRequest requestDTO, boolean isCount);

    NotificationStatsResponse getStatsByUser(UUID userId);

    NotificationDTO findOneById(UUID id);

    NotificationDTO triggerSend(NotificationDTO notificationDTO);

    void executeSend(NotificationDTO notificationDTO) throws FirebaseMessagingException;

    NotificationDTO sendNewComerNotification(UUID userId);
}
