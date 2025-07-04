package com.example.sbt.module.token.repository;

import com.example.sbt.module.token.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
    @Modifying
    @Query(value = "update auth_token set status = :status, updated_at = now() " +
            "where user_id = :userId and type = :type and status <> :status", nativeQuery = true)
    void updateStatusByUserIdAndType(UUID userId, String type, String status);

    @Modifying
    @Query(value = "update auth_token set status = :status, updated_at = now() " +
            "where user_id = :userId and type in :types and status <> :status", nativeQuery = true)
    void updateStatusByUserIdAndTypes(UUID userId, List<String> types, String status);

    @Modifying
    @Query(value = "update auth_token set status = :status, updated_at = now() " +
            "where user_id = :userId and status <> :status", nativeQuery = true)
    void updateStatusByUserId(UUID userId, String status);

    Optional<AuthToken> findTopByIdAndStatusAndExpiresAtAfter(UUID tokenId, String status, Instant now);

    void deleteByExpiresAtBefore(Instant expiresAt);
}
