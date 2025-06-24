package com.example.sbt.module.file.repository;

import com.example.sbt.module.file.entity.FileObjectPending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileObjectPendingRepository extends JpaRepository<FileObjectPending, UUID> {

    Optional<FileObjectPending> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

}
