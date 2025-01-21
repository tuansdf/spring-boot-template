package com.example.springboot.modules.role;

import com.example.springboot.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    void deleteAllByUserId(UUID userId);

}
