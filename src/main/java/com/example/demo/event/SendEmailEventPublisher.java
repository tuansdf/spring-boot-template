package com.example.demo.event;

import com.example.demo.common.constant.RedisKey;
import com.example.demo.common.dto.RequestContextHolder;
import com.example.demo.module.email.dto.SendEmailStreamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendEmailEventPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(UUID emailId) {
        SendEmailStreamRequest request = SendEmailStreamRequest.builder()
                .requestContext(RequestContextHolder.get())
                .emailId(emailId)
                .build();
        ObjectRecord<String, SendEmailStreamRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(RedisKey.SEND_EMAIL_STREAM);
        redisTemplate.opsForStream().add(data);
    }

}
