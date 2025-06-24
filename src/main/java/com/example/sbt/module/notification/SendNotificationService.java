package com.example.sbt.module.notification;

import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface SendNotificationService {

    void send(SendNotificationRequest request) throws FirebaseMessagingException;

    void subscribeTopic(SendNotificationRequest request) throws FirebaseMessagingException;
}
