package com.example.sbt.module.backgroundtask.repository;

import com.example.sbt.module.backgroundtask.entity.BackgroundTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BackgroundTaskRepository extends JpaRepository<BackgroundTask, UUID> {
    @Query(value = "update background_task set status = :status, file_id = :fileId, updated_at = now() where id = :id", nativeQuery = true)
    @Modifying
    void updateStatusById(UUID id, BackgroundTask.Status status, UUID fileId);

    @Query(value = "update background_task set status = :status, updated_at = now() where id = :id", nativeQuery = true)
    @Modifying
    void updateStatusById(UUID id, BackgroundTask.Status status);

    Optional<BackgroundTask> findTopByCacheKeyAndTypeAndStatus(String cacheKey, String type, BackgroundTask.Status status);
}
