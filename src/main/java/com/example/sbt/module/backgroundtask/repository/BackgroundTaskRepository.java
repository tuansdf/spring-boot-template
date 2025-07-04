package com.example.sbt.module.backgroundtask.repository;

import com.example.sbt.module.backgroundtask.entity.BackgroundTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface BackgroundTaskRepository extends JpaRepository<BackgroundTask, UUID> {
    @Query(value = "update background_task set status = :status, file_id = :fileId, cache_key = :cacheKey, updated_at = now() where id = :id", nativeQuery = true)
    @Modifying
    void updateStatusById(UUID id, String status, UUID fileId, String cacheKey);

    @Query(value = "update background_task set status = :status, updated_at = now() where id = :id", nativeQuery = true)
    @Modifying
    void updateStatusById(UUID id, String status);

    @Query(value = "select * from background_task where type = :type and status = :status and created_at > :createdAt order by id asc limit 1", nativeQuery = true)
    Optional<BackgroundTask> findTopRecentByCacheKey(String cacheKey, String type, String status, Instant createdAt);
}
