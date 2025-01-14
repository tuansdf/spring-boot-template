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

    @Query(value = "select r.code from user_role ur " +
            "left join role r on (r.id = ur.role_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findAllCodesByUserId(UUID userId);

    @Query(value = "select r.* from user_role ur " +
            "left join role r on (r.id = ur.role_id) " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<Role> findAllByUserId(UUID userId);

    Optional<Role> findTopByCode(String code);

    boolean existsByCode(String code);

}
