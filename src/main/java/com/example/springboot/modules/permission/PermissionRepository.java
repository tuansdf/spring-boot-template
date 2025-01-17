package com.example.springboot.modules.permission;

import com.example.springboot.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = "select p.code from role_permission rp " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where rp.role_id = :roleId", nativeQuery = true)
    List<String> findAllCodesByRoleId(Long roleId);

    @Query(value = "select p.code from user_role ur " +
            "inner join role_permission rp on (rp.role_id = ur.role_id) " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select p.* from role_permission rp " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where rp.role_id = :roleId", nativeQuery = true)
    List<Permission> findAllByRoleId(Long roleId);

    @Query(value = "select p.* from user_role ur " +
            "inner join role_permission rp on (rp.role_id = ur.role_id) " +
            "inner join permission p on (p.id = rp.permission_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<Permission> findAllByUserId(UUID userId);

    Optional<Permission> findTopByCode(String code);

    boolean existsByCode(String code);

}
