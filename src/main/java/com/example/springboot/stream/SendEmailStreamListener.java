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
            log.info("XSTART");

            emailService.executeSend(message.getValue().getEmailId());
        } catch (Exception e) {
            log.error("XERROR", e);
        } finally {
            redisTemplate.opsForStream().acknowledge(RedisKey.SEND_EMAIL_STREAM, message);
            redisTemplate.opsForStream().delete(RedisKey.SEND_EMAIL_STREAM, message.getId());

            log.info("XEND");
            RequestContextHolder.clear();
        }
    }

}
