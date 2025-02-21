package com.example.demo.stream;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.constants.RedisKey;
import com.example.demo.modules.email.EmailService;
import com.example.demo.modules.email.dtos.SendEmailStreamRequest;
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
            SendEmailStreamRequest request = message.getValue();
            RequestContextHolder.set(request.getRequestContext());
            log.info("XSTART");

            // TODO: FIX
            Thread.sleep(1000);

            emailService.executeSend(request.getEmailId());
        } catch (Exception e) {
            log.error("XERROR", e);
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(RedisKey.SEND_EMAIL_STREAM, message);
                redisTemplate.opsForStream().delete(RedisKey.SEND_EMAIL_STREAM, message.getId());

                log.info("XEND");
            } catch (Exception ignore) {
            }
            RequestContextHolder.clear();
        }
    }

}
