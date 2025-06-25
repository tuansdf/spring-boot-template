package com.example.sbt.module.notification;

import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.scheduling.annotation.Async;

public interface SendNotificationService {

    void send(SendNotificationRequest request) throws FirebaseMessagingException;

    @Async
    void sendAsync(SendNotificationRequest request) throws FirebaseMessagingException;

    void subscribeTopic(SendNotificationRequest request) throws FirebaseMessagingException;

    @Async
    void subscribeTopicAsync(SendNotificationRequest request) throws FirebaseMessagingException;

}
