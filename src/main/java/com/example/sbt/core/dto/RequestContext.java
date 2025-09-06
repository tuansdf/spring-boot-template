package com.example.sbt.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestContext {
    private String tenantId;
    private String requestId;
    private Locale locale;
    private UUID userId;
    private String username;
}
