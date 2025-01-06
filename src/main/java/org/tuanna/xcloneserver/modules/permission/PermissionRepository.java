package org.tuanna.xcloneserver.modules.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.Permission;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = "select p.* from role_permission rp " +
            "left join permission p on (p.id = rp.permission_id) " +
            "where rp.role_id = :roleId", nativeQuery = true)
    List<Permission> findAllByRoleId(Long roleId);

    @Query(value = "select p.* from user_role ur " +
            "left join role_permission rp on (rp.role_id = ur.role_id) " +
            "left join permission p on (p.id = rp.permission_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<Permission> findAllByUserId(UUID userId);

}
