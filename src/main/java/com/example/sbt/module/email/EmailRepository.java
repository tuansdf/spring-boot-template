package com.example.sbt.module.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {

    @Query(value = "select 'true' from email e where e.user_id = :userId and e.type = :type and e.created_at > :fromTime limit 1", nativeQuery = true)
    Boolean existsRecentByUserIdAndType(UUID userId, Integer type, Instant fromTime);

}
