package com.example.sbt.common.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.Locale;
import java.util.UUID;

@Value
@Builder
public class RequestContext {
    @With
    String tenantId;
    @With
    String requestId;
    @With
    Locale locale;
    @With
    UUID userId;
    @With
    String username;
    @With
    String ip;
}
