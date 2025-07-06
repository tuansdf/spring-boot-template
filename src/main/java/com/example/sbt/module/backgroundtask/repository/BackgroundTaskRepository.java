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
    void updateStatusById(UUID id, String status, UUID fileId);

    @Query(value = "update background_task set status = :status, updated_at = now() where id = :id", nativeQuery = true)
    @Modifying
    void updateStatusById(UUID id, String status);

    @Query(value = "select * from background_task where cache_key = :cacheKey and type = :type and status = :status order by id asc limit 1", nativeQuery = true)
    Optional<BackgroundTask> findTopByCacheKeyAndTypeAndStatus(String cacheKey, String type, String status);
}
