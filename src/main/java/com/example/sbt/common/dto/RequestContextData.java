package com.example.sbt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestContextData {

    private String tenantId;
    private String requestId;
    private Locale locale;
    private UUID userId;
    private List<String> permissions;

}
