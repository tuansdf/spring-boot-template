package com.example.sbt.module.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileObjectRepository extends JpaRepository<FileObject, UUID> {
}
