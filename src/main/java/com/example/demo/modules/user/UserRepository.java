package com.example.demo.modules.user;

import com.example.demo.entities.User;
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

    @Modifying
    @Query(value = "update _user set password = :password, updated_at = now() where id = :userId", nativeQuery = true)
    void updatePasswordByUserId(UUID userId, String password);

    @Modifying
    @Query(value = "update _user set status = :status, updated_at = now() where id = :userId", nativeQuery = true)
    void updateStatusByUserId(UUID userId, Integer status);

    @Query(value = "select u.status from _user u where u.id = :userId limit 1", nativeQuery = true)
    String findTopStatusByUserId(UUID userId);

}
