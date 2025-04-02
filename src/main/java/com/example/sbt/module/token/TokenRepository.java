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
            "where owner_id = :userId and type = :type and status <> :status", nativeQuery = true)
    void updateStatusByOwnerIdAndType(UUID userId, String type, String status);

    @Modifying
    @Query(value = "update token set status = :status, updated_at = now() " +
            "where owner_id = :userId and type in :types and status <> :status", nativeQuery = true)
    void updateStatusByOwnerIdAndTypes(UUID userId, List<String> types, String status);

    @Modifying
    @Query(value = "update token set status = :status, updated_at = now() " +
            "where owner_id = :userId and status <> :status", nativeQuery = true)
    void updateStatusByOwnerId(UUID userId, String status);

    Optional<Token> findTopByIdAndStatusAndExpiresAtAfter(UUID tokenId, String status, Instant now);

}
