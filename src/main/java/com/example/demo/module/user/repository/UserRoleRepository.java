package com.example.demo.module.user.repository;

import com.example.demo.module.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    void deleteAllByUserId(UUID userId);

}
