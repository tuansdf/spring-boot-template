package com.example.demo.dtos;

import lombok.*;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RequestContext {

    private String tenantId;
    private String requestId;
    private Locale locale;
    private UUID userId;
    private Set<String> permissions;

}
