package com.example.sbt.event.listener;

import com.example.sbt.core.constant.EventKey;
import com.example.sbt.core.dto.RequestContextHolder;
import com.example.sbt.event.dto.SendNotificationEventRequest;
import com.example.sbt.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendNotificationEventListener implements StreamListener<String, ObjectRecord<String, SendNotificationEventRequest>> {
    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, SendNotificationEventRequest> message) {
        try {
            SendNotificationEventRequest request = message.getValue();
            RequestContextHolder.set(request.getRequestContext());
            log.info("SendNotificationEventListener.start {}", request.getNotification().getId());

            notificationService.executeSend(request.getNotification());
        } catch (Exception e) {
            log.error("SendNotificationEventListener.error {}", e.toString());
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(EventKey.SEND_NOTIFICATION, message);
                redisTemplate.opsForStream().delete(EventKey.SEND_NOTIFICATION, message.getId());
                log.info("SendNotificationEventListener.end");
            } catch (Exception ignore) {
            }
            RequestContextHolder.clear();
        }
    }
}
