package com.example.sbt.module.user.repository;

import com.example.sbt.module.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    void deleteAllByUserId(UUID userId);

    void deleteAllByUserIdAndRoleIdIn(UUID userId, List<UUID> roleIds);

}
