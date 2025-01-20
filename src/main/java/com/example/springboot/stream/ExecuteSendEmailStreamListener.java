package com.example.springboot.stream;

import com.example.springboot.constants.StreamKey;
import com.example.springboot.modules.email.dtos.ExecuteSendEmailStreamRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExecuteSendEmailStreamListener implements StreamListener<String, ObjectRecord<String, ExecuteSendEmailStreamRequest>> {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public static void add(ReactiveRedisTemplate<String, Object> redisTemplate, ExecuteSendEmailStreamRequest request) {
        ObjectRecord<String, ExecuteSendEmailStreamRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(StreamKey.EXECUTE_SEND_EMAIL_STREAM);
        redisTemplate.opsForStream().add(data).block();
    }

    @Override
    public void onMessage(ObjectRecord<String, ExecuteSendEmailStreamRequest> message) {

    }

}
