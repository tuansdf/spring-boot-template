package com.example.sbt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestContext {

    private String tenantId;
    private String requestId;
    private Locale locale;
    private UUID userId;
    private Set<String> permissions;

}
