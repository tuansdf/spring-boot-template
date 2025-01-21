package com.example.springboot.dtos;

import lombok.*;

import java.util.Locale;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RequestContext {

    private String tenantId;
    private String requestId;
    private Locale locale;
    private String userId;
    private Set<String> permissions;

}
