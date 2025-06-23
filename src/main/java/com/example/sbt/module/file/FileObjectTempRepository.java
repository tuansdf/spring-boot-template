package com.example.sbt.module.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileObjectTempRepository extends JpaRepository<FileObjectTemp, UUID> {

    Optional<FileObjectTemp> findTopByIdAndCreatedBy(UUID id, UUID createdBy);

}
