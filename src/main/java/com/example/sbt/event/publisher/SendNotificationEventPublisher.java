package com.example.sbt.event.publisher;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.event.dto.SendNotificationEventRequest;
import com.example.sbt.module.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendNotificationEventPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(NotificationDTO notification) {
        SendNotificationEventRequest request = SendNotificationEventRequest.builder()
                .requestContextData(RequestContext.get())
                .notification(notification)
                .build();
        ObjectRecord<String, SendNotificationEventRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(EventKey.SEND_NOTIFICATION);
        redisTemplate.opsForStream().add(data);
    }

}
