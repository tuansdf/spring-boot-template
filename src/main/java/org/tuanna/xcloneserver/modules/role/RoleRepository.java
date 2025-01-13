package org.tuanna.xcloneserver.modules.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tuanna.xcloneserver.entities.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "select r.code from UserRole ur " +
            "left join Role r on (r.id = ur.roleId) " +
            "where ur.userId = :userId")
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select r from UserRole ur " +
            "left join Role r on (r.id = ur.roleId) " +
            "where ur.userId = :userId")
    List<Role> findAllByUserId(UUID userId);

    Optional<Role> findTopByCode(String code);

}
