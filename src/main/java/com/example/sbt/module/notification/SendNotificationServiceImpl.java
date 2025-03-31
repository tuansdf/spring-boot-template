package com.example.sbt.module.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class SendNotificationServiceImpl implements SendNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void send(Message message) throws FirebaseMessagingException {
        firebaseMessaging.send(message);
    }

}
