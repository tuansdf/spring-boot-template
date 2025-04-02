package com.example.sbt.module.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Modifying
    @Query(value = "update token set status = :status, updated_at = now() " +
            "where owner_id = :userId and created_at < now() and type = :type and status <> :status", nativeQuery = true)
    void updateStatusByOwnerIdAndTypeAndCreatedAtBefore(UUID userId, String type, String status);

    @Modifying
    @Query(value = "update token set status = :status, updated_at = now() " +
            "where owner_id = :userId and created_at < now() and type in :types and status <> :status", nativeQuery = true)
    void updateStatusByOwnerIdAndTypesAndCreatedAtBefore(UUID userId, List<String> types, String status);

    Optional<Token> findTopByIdAndStatusAndExpiresAtAfter(UUID tokenId, String status, Instant now);

}
