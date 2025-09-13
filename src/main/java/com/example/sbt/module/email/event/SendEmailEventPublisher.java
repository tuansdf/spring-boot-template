package com.example.sbt.module.email.event;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.module.email.dto.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendEmailEventPublisher {
    private final StringRedisTemplate redisTemplate;

    public void publish(EmailDTO email) {
        SendEmailEventRequest request = SendEmailEventRequest.builder()
                .requestContext(RequestContextHolder.get())
                .email(email)
                .build();
        ObjectRecord<String, SendEmailEventRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(EventKey.SEND_EMAIL);
        redisTemplate.opsForStream().add(data);
    }
}
