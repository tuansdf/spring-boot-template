package com.example.sbt.module.file.repository;

import com.example.sbt.module.file.entity.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileObjectRepository extends JpaRepository<FileObject, UUID> {
    @Query(value = "select file_path from file_object where id in :ids and created_by = :createdBy", nativeQuery = true)
    List<String> findAllFilePathsByIdInAndCreatedBy(List<UUID> ids, UUID createdBy);

    void deleteByIdInAndCreatedBy(List<UUID> ids, UUID createdBy);

    Optional<FileObject> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

    Optional<FileObject> findTopByCacheKeyOrderByIdDesc(String cacheKey);

    @Query(value = "update file_object set file_path = :filePath, updated_at = now() where cache_key = :cacheKey", nativeQuery = true)
    @Modifying
    void updateFilePathByCacheKey(String cacheKey, String filePath);
}
