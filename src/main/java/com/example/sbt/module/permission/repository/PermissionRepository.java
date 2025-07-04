package com.example.sbt.module.permission.repository;

import com.example.sbt.module.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    @Query(value = "select permission_id from role_permission where role_id = :roleId", nativeQuery = true)
    List<UUID> findAllIdsByRoleId(UUID roleId);

    @Query(value = "select p.code from role_permission rp " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where rp.role_id = :roleId", nativeQuery = true)
    List<String> findAllCodesByRoleId(UUID roleId);

    @Query(value = "select p.code from user_role ur " +
            "inner join role_permission rp on (rp.role_id = ur.role_id) " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select p.* from role_permission rp " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where rp.role_id = :roleId", nativeQuery = true)
    List<Permission> findAllByRoleId(UUID roleId);

    @Query(value = "select p.* from user_role ur " +
            "inner join role_permission rp on (rp.role_id = ur.role_id) " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<Permission> findAllByUserId(UUID userId);

    Optional<Permission> findTopByCode(String code);

    boolean existsByCode(String code);

    long countByIdIn(List<UUID> ids);
}
