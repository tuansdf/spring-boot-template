package com.example.sbt.module.backgroundtask.service;

import com.example.sbt.module.backgroundtask.dto.BackgroundTaskDTO;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface BackgroundTaskService {
    BackgroundTaskDTO init(String type);

    BackgroundTaskDTO findOneRecentByCacheKey(String cacheKey, String type);

    BackgroundTaskDTO findOneById(UUID id);

    BackgroundTaskDTO findOneByIdOrThrow(UUID id);

    void updateStatus(UUID id, String status, UUID fileId, String cacheKey);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void updateStatusWithTrans(UUID id, String status, UUID fileId, String cacheKey);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void updateStatusWithTrans(UUID id, String status);
}
