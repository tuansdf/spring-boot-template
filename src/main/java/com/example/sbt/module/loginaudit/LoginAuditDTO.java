package com.example.sbt.module.loginaudit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginAuditDTO {

    private UUID id;
    private UUID userId;
    private Boolean isSuccess;
    private Instant createdAt;
    private Instant updatedAt;

}
