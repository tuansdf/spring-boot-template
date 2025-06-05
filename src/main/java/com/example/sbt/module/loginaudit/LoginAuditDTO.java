package com.example.sbt.module.loginaudit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginAuditDTO {

    private UUID id;
    private UUID userId;
    private Boolean isSuccess;
    private Instant createdAt;
    private Instant updatedAt;

}
