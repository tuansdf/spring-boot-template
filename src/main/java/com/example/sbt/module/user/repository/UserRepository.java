package com.example.sbt.module.user.repository;

import com.example.sbt.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findTopByUsername(String username);

    Optional<User> findTopByEmail(String email);

    Optional<User> findTopByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByIdAndStatus(UUID id, String status);

    Optional<User> findTopByIdAndStatus(UUID id, String status);

    @Modifying
    @Query(value = "update _user set status = :status, updated_at = now() where id = :userId", nativeQuery = true)
    void updateStatusByUserId(UUID userId, String status);

    @Query(value = "select status from _user where id = :userId order by id asc limit 1", nativeQuery = true)
    String findTopStatusByUserId(UUID userId);

}
