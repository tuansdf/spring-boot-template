package com.example.sbt.module.loginaudit.repository;

import com.example.sbt.module.loginaudit.entity.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, UUID> {
    @Query(value = "select count(*) from login_audit la where la.user_id = :userId and la.is_success = false and la.created_at >= :fromTime " +
            "and la.created_at >= (select la1.created_at from login_audit la1 where la1.user_id = :userId and la1.is_success = true order by la1.created_at desc limit 1)", nativeQuery = true)
    Long countRecentlyFailedAttemptsByUserId(UUID userId, Instant fromTime);
}
