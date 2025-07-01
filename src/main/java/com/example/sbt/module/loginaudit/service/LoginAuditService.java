package com.example.sbt.module.loginaudit.service;

import com.example.sbt.module.loginaudit.dto.LoginAuditDTO;

import java.time.Instant;
import java.util.UUID;

public interface LoginAuditService {

    void add(LoginAuditDTO audit);

    void add(UUID userId, boolean isSuccess);

    long countRecentlyFailedAttemptsByUserId(UUID userId, Instant fromTime);
}
