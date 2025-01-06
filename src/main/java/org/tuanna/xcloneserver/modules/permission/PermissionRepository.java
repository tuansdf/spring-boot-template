package org.tuanna.xcloneserver.modules.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.Permission;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = "select p.code from RolePermission rp " +
            "left join Permission p on (p.id = rp.permissionId) " +
            "where rp.roleId = :roleId")
    List<String> findAllCodesByRoleId(Long roleId);

    @Query(value = "select p.code from UserRole ur " +
            "left join RolePermission rp on (rp.roleId = ur.roleId) " +
            "left join Permission p on (p.id = rp.permissionId) " +
            "where ur.userId = :userId")
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select p from RolePermission rp " +
            "left join Permission p on (p.id = rp.permissionId) " +
            "where rp.roleId = :roleId")
    List<Permission> findAllByRoleId(Long roleId);

    @Query(value = "select p from UserRole ur " +
            "left join RolePermission rp on (rp.roleId = ur.roleId) " +
            "left join Permission p on (p.id = rp.permissionId) " +
            "where ur.userId = :userId")
    List<Permission> findAllByUserId(UUID userId);

}
