package com.example.sbt.features.loginaudit.service;

import com.example.sbt.features.loginaudit.dto.LoginAuditDTO;

import java.time.Instant;
import java.util.UUID;

public interface LoginAuditService {
    void add(LoginAuditDTO audit);

    void add(UUID userId, boolean isSuccess);

    long countRecentlyFailedAttemptsByUsername(String username, Instant fromTime);
}
