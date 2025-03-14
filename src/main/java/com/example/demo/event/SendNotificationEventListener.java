package com.example.demo.event;

import com.example.demo.common.constant.RedisKey;
import com.example.demo.common.dto.RequestContextHolder;
import com.example.demo.module.notification.NotificationService;
import com.example.demo.module.notification.dto.SendNotificationStreamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendNotificationEventListener implements StreamListener<String, ObjectRecord<String, SendNotificationStreamRequest>> {

    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, SendNotificationStreamRequest> message) {
        try {
            SendNotificationStreamRequest request = message.getValue();
            RequestContextHolder.set(request.getRequestContext());
            log.info("XSTART");

            // TODO: FIX
            Thread.sleep(1000);

            notificationService.executeSend(request.getNotificationId());
        } catch (Exception e) {
            log.error("XERROR", e);
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(RedisKey.SEND_NOTIFICATION_STREAM, message);
                redisTemplate.opsForStream().delete(RedisKey.SEND_NOTIFICATION_STREAM, message.getId());

                log.info("XEND");
            } catch (Exception ignore) {
            }
            RequestContextHolder.clear();
        }
    }

}
