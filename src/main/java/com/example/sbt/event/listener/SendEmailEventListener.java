package com.example.sbt.event.listener;

import com.example.sbt.core.constant.EventKey;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.event.dto.SendEmailEventRequest;
import com.example.sbt.module.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendEmailEventListener implements StreamListener<String, ObjectRecord<String, SendEmailEventRequest>> {
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, SendEmailEventRequest> message) {
        try {
            SendEmailEventRequest request = message.getValue();
            RequestContext.set(request.getRequestContext());
            log.info("SendEmailEventListener.start ");

            emailService.executeSend(request.getEmail());
        } catch (Exception e) {
            log.error("SendEmailEventListener.error {}", e.toString());
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(EventKey.SEND_EMAIL, message);
                redisTemplate.opsForStream().delete(EventKey.SEND_EMAIL, message.getId());
                log.info("SendEmailEventListener.end ");
            } catch (Exception ignore) {
            }
            RequestContext.clear();
        }
    }
}
