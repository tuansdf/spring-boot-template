package com.example.sbt.module.loginaudit.service;

import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.module.loginaudit.dto.LoginAuditDTO;
import com.example.sbt.module.loginaudit.entity.LoginAudit;
import com.example.sbt.module.loginaudit.repository.LoginAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class LoginAuditServiceImpl implements LoginAuditService {
    private final CommonMapper commonMapper;
    private final LoginAuditRepository loginAuditRepository;

    @Override
    public void add(LoginAuditDTO audit) {
        if (audit == null) return;
        audit.setId(null);
        loginAuditRepository.save(commonMapper.toEntity(audit));
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void add(UUID userId, boolean isSuccess) {
        LoginAudit audit = new LoginAudit();
        audit.setUserId(userId);
        audit.setIsSuccess(isSuccess);
        loginAuditRepository.save(audit);
    }

    @Override
    public long countRecentlyFailedAttemptsByUserId(String username, Instant fromTime) {
        return ConversionUtils.safeToLong(loginAuditRepository.countRecentlyFailedAttemptsByUserId(username, fromTime));
    }
}