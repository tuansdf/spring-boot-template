package com.example.sbt.module.role.repository;

import com.example.sbt.module.role.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    void deleteAllByRoleId(UUID roleId);

    void deleteAllByRoleIdAndPermissionIdIn(UUID roleId, List<UUID> permissionIds);
}
