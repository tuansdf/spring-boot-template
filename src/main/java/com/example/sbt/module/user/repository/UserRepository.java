package com.example.sbt.module.user.repository;

import com.example.sbt.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findTopByUsername(String username);

    Optional<User> findTopByEmail(String email);

    Optional<User> findTopByEmailAndIsEnabled(String email, Boolean isEnabled);

    Optional<User> findTopByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByIdAndIsEnabled(UUID id, Boolean isEnabled);

    Optional<User> findTopByIdAndIsEnabled(UUID id, Boolean isEnabled);
}
