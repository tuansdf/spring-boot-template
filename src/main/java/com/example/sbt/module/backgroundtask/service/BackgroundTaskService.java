package com.example.sbt.module.backgroundtask.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;

import java.util.UUID;

public interface BackgroundTaskService {
    BackgroundTaskDTO init(String cacheKey, String type, RequestContext requestContext);

    BackgroundTaskDTO findOneByCacheKey(String cacheKey, String type);

    boolean completeByCacheKeyIfExist(String cacheKey, String type, UUID taskId);

    BackgroundTaskDTO findOneById(UUID id);

    BackgroundTaskDTO findOneByIdOrThrow(UUID id);

    void updateStatus(UUID id, String status, UUID fileId);

    void updateStatus(UUID id, String status);
}
