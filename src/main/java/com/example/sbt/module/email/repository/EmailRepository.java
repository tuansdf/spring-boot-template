package com.example.sbt.module.email.repository;

import com.example.sbt.module.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {
    Optional<Email> findTopByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndTypeAndCreatedAtAfter(UUID userId, String type, Instant fromTime);
}
