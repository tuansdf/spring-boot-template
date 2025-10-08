package com.example.sbt.features.backgroundtask.service;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.features.backgroundtask.dto.BackgroundTaskDTO;
import com.example.sbt.features.backgroundtask.entity.BackgroundTask;

import java.util.UUID;

public interface BackgroundTaskService {
    BackgroundTaskDTO init(String cacheKey, String type, RequestContext requestContext);

    BackgroundTaskDTO findOneByCacheKey(String cacheKey, String type);

    boolean completeByCacheKeyIfExist(String cacheKey, String type, UUID taskId);

    BackgroundTaskDTO findOneById(UUID id);

    BackgroundTaskDTO findOneByIdOrThrow(UUID id);

    void updateStatusIfCurrent(UUID id, BackgroundTask.Status status, BackgroundTask.Status current, UUID fileId);

    void updateStatusIfCurrent(UUID id, BackgroundTask.Status status, BackgroundTask.Status current);
}
