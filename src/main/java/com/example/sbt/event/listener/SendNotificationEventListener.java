package com.example.sbt.event.listener;

import com.example.sbt.common.constant.RedisKey;
import com.example.sbt.common.dto.RequestContextHolder;
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
            RequestContextHolder.set(request.getRequestContext());
            log.info("SendNotificationEventListener.start");

            // TODO: FIX
            Thread.sleep(1000);

            notificationService.executeSend(request.getNotificationId());
        } catch (Exception e) {
            log.error("SendNotificationEventListener.error", e);
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(RedisKey.SEND_NOTIFICATION_STREAM, message);
                redisTemplate.opsForStream().delete(RedisKey.SEND_NOTIFICATION_STREAM, message.getId());

                log.info("SendNotificationEventListener.end");
            } catch (Exception ignore) {
            }
            RequestContextHolder.clear();
        }
    }

}
