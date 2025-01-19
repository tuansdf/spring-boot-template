package com.example.springboot.dtos;

import lombok.*;

import java.util.List;
import java.util.Locale;

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
    private List<String> permissions;

}
