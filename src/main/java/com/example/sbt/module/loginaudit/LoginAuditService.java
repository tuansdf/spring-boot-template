package com.example.sbt.module.loginaudit;

import java.time.Instant;
import java.util.UUID;

public interface LoginAuditService {

    void add(LoginAuditDTO audit);

    void add(UUID userId, boolean isSuccess);

    long countRecentlyFailedAttemptsByUserId(UUID userId, Instant fromTime);
}
