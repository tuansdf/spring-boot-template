package com.example.springboot.stream;

import com.example.springboot.configs.RequestContextHolder;
import com.example.springboot.constants.RedisKey;
import com.example.springboot.modules.email.EmailService;
import com.example.springboot.modules.email.dtos.SendEmailStreamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendEmailStreamListener implements StreamListener<String, ObjectRecord<String, SendEmailStreamRequest>> {

    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, SendEmailStreamRequest> message) {
        try {
            RequestContextHolder.set(message.getValue().getRequestContext());
            log.info("Start SendEmailStreamListener");
            emailService.executeSend(message.getValue().getEmailId());
        } catch (Exception e) {
            log.error("SendEmailStreamListener", e);
        } finally {
            log.info("End SendEmailStreamListener");
            redisTemplate.opsForStream().acknowledge(RedisKey.SEND_EMAIL_STREAM, message);
            RequestContextHolder.clear();
        }
    }

}
