package com.example.springboot.stream;

import com.example.springboot.configs.RequestContextHolder;
import com.example.springboot.constants.StreamKey;
import com.example.springboot.modules.email.EmailService;
import com.example.springboot.modules.email.dtos.ExecuteSendEmailStreamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExecuteSendEmailStreamListener implements StreamListener<String, ObjectRecord<String, ExecuteSendEmailStreamRequest>> {

    private final EmailService emailService;
//    private final RedisTemplate<String, Object> redisTemplate;

    public static void add(RedisTemplate<String, Object> redisTemplate, ExecuteSendEmailStreamRequest request) {
        ObjectRecord<String, ExecuteSendEmailStreamRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(StreamKey.EXECUTE_SEND_EMAIL_STREAM);
        redisTemplate.opsForStream().add(data);
    }

    @Override
    public void onMessage(ObjectRecord<String, ExecuteSendEmailStreamRequest> message) {
        try {
            log.info("IN LISTENER");
            RequestContextHolder.set(message.getValue().getRequestContext());
            emailService.executeSend(message.getValue().getEmailId());
        } finally {
//            redisTemplate.opsForStream().acknowledge(StreamKey.EXECUTE_SEND_EMAIL_STREAM, message);
            RequestContextHolder.clear();
        }
    }

}
