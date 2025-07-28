package com.example.sbt.event.publisher;

import com.example.sbt.core.constant.EventKey;
import com.example.sbt.core.dto.RequestContextHolder;
import com.example.sbt.event.dto.ExportUserEventRequest;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
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

    public void publish(UUID id, SearchUserRequestDTO requestDTO) {
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
