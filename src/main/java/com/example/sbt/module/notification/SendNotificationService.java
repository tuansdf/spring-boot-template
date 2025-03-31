package com.example.sbt.module.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

public interface SendNotificationService {

    void send(Message message) throws FirebaseMessagingException;

}
