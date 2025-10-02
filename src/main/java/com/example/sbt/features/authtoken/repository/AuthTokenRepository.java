package com.example.sbt.features.authtoken.repository;

import com.example.sbt.features.authtoken.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
    @Modifying
    @Query(value = "update auth_token set valid_from = now(), updated_at = now() where user_id = :userId and type = :type", nativeQuery = true)
    void invalidateByUserIdAndType(UUID userId, AuthToken.Type type);

    @Modifying
    @Query(value = "update auth_token set valid_from = now(), updated_at = now() where user_id = :userId and type in :types", nativeQuery = true)
    void invalidateByUserIdAndTypes(UUID userId, List<AuthToken.Type> types);

    @Modifying
    @Query(value = "update auth_token set valid_from = now(), updated_at = now() where user_id = :userId", nativeQuery = true)
    void invalidateByUserId(UUID userId);

    Optional<AuthToken> findTopByUserIdAndType(UUID userId, AuthToken.Type type);
}
