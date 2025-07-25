package com.example.sbt.module.notification.service;

import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class SendNotificationServiceImpl implements SendNotificationService {
    private final int TOKEN_BATCH_SIZE = 500;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(SendNotificationRequest request) throws FirebaseMessagingException {
        if (request == null) return;

        if (StringUtils.isNotBlank(request.getTopic())) {
            Message message = Message.builder()
                    .setTopic(request.getTopic())
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody())
                            .build())
                    .build();
            firebaseMessaging.send(message);
            return;
        }

        if (CollectionUtils.isEmpty(request.getTokens())) return;

        int tokenSize = request.getTokens().size();
        for (int i = 0; i < tokenSize; i += TOKEN_BATCH_SIZE) {
            int to = Math.min(i + TOKEN_BATCH_SIZE, tokenSize);
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(request.getTokens().subList(i, to))
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody())
                            .build())
                    .build();
            firebaseMessaging.sendEachForMulticast(message);
        }
    }

    @Async
    @Override
    public void sendAsync(SendNotificationRequest request) throws FirebaseMessagingException {
        send(request);
    }

    @Override
    public void subscribeTopic(SendNotificationRequest request) throws FirebaseMessagingException {
        if (request == null || StringUtils.isBlank(request.getTopic())) return;

        if (CollectionUtils.isEmpty(request.getTokens())) return;

        int tokenSize = request.getTokens().size();
        for (int i = 0; i < tokenSize; i += TOKEN_BATCH_SIZE) {
            int to = Math.min(i + TOKEN_BATCH_SIZE, tokenSize);
            firebaseMessaging.subscribeToTopic(request.getTokens().subList(i, to), request.getTopic());
        }
    }

    @Async
    @Override
    public void subscribeTopicAsync(SendNotificationRequest request) throws FirebaseMessagingException {
        subscribeTopic(request);
    }
}
