package com.example.sbt.module.file.repository;

import com.example.sbt.module.file.entity.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FileObjectRepository extends JpaRepository<FileObject, UUID> {

    @Query(value = "select file_path from file_object where id in :ids and created_by = :createdBy", nativeQuery = true)
    Set<String> findAllPathsByIdInAndCreatedBy(Set<UUID> ids, UUID createdBy);

    @Modifying
    @Query(value = "delete from file_object where id in :ids and created_by = :createdBy", nativeQuery = true)
    void deleteAllByIdInAndCreatedBy(Set<UUID> ids, UUID createdBy);

    Optional<FileObject> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

}
