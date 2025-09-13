package com.example.sbt.module.user.event;

import com.example.sbt.common.constant.EventKey;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.module.user.dto.SearchUserRequest;
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
public class ExportUserEventPublisher {
    private final StringRedisTemplate redisTemplate;

    public void publish(UUID id, SearchUserRequest requestDTO) {
        ExportUserEventRequest request = ExportUserEventRequest.builder()
                .requestContext(RequestContextHolder.get())
                .searchRequest(requestDTO)
                .backgroundTaskId(id)
                .build();
        ObjectRecord<String, ExportUserEventRequest> data = StreamRecords.newRecord()
                .ofObject(request)
                .withStreamKey(EventKey.EXPORT_USER);
        redisTemplate.opsForStream().add(data);
    }
}
