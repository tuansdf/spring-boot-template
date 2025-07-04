package com.example.sbt.module.file.repository;

import com.example.sbt.module.file.entity.FileObjectPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileObjectPendingRepository extends JpaRepository<FileObjectPending, UUID> {
    Optional<FileObjectPending> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

    @Query(value = "select file_path from file_object_pending where expires_at < :expiresAt", nativeQuery = true)
    List<String> findAllFilePathsByExpiresAtBefore(Instant expiresAt);

    void deleteByExpiresAtBefore(Instant expiresAt);
}
