package com.example.sbt.module.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileObjectPendingRepository extends JpaRepository<FileObjectPending, UUID> {

    Optional<FileObjectPending> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

}
