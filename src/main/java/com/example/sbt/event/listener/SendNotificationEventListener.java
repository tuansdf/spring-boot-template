package com.example.sbt.event.listener;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.event.dto.SendNotificationEventRequest;
import com.example.sbt.module.notification.NotificationService;
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
            RequestHolder.setContext(request.getRequestContext());
            log.info("SendNotificationEventListener.start ");

            notificationService.executeSend(request.getNotification());
        } catch (Exception e) {
            log.error("SendNotificationEventListener.error ", e);
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(EventKey.SEND_NOTIFICATION, message);
                redisTemplate.opsForStream().delete(EventKey.SEND_NOTIFICATION, message.getId());

                log.info("SendNotificationEventListener.end ");
            } catch (Exception ignore) {
            }
            RequestHolder.clear();
        }
    }

}
