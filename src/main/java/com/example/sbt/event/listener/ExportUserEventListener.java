package com.example.sbt.event.listener;

import com.example.sbt.core.constant.EventKey;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.event.dto.ExportUserEventRequest;
import com.example.sbt.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExportUserEventListener implements StreamListener<String, ObjectRecord<String, ExportUserEventRequest>> {
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    @Override
    public void onMessage(ObjectRecord<String, ExportUserEventRequest> message) {
        try {
            ExportUserEventRequest request = message.getValue();
            RequestContext.set(request.getRequestContext());
            log.info("ExportUserEventListener.start ");

            userService.handleExportTask(request.getBackgroundTaskId(), request.getSearchRequest());
        } catch (Exception e) {
            log.error("ExportUserEventListener.error ", e);
        } finally {
            try {
                redisTemplate.opsForStream().acknowledge(EventKey.EXPORT_USER, message);
                redisTemplate.opsForStream().delete(EventKey.EXPORT_USER, message.getId());
                log.info("ExportUserEventListener.end ");
            } catch (Exception ignore) {
            }
            RequestContext.clear();
        }
    }
}
