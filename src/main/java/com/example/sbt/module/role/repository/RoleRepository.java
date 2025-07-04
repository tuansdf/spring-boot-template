package com.example.sbt.module.role.repository;

import com.example.sbt.module.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query(value = "select role_id from user_role where user_id = :userId", nativeQuery = true)
    List<UUID> findAllIdsByUserId(UUID userId);

    @Query(value = "select r.code from user_role ur " +
            "inner join role r on (r.id = ur.role_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select r.* from user_role ur " +
            "inner join role r on (r.id = ur.role_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<Role> findAllByUserId(UUID userId);

    Optional<Role> findTopByCode(String code);

    boolean existsByCode(String code);

    long countByIdIn(List<UUID> ids);
}
