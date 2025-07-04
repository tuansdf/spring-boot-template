package com.example.sbt.module.email.repository;

import com.example.sbt.module.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {
    Optional<Email> findTopByIdAndUserId(UUID id, UUID userId);

    @Query(value = "select 1 from email e where e.user_id = :userId and e.type = :type and e.created_at > :fromTime limit 1", nativeQuery = true)
    Integer existsRecentByUserIdAndType(UUID userId, String type, Instant fromTime);
}
