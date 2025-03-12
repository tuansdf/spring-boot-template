package com.example.demo.module.token.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TokenDTO {

    private UUID id;
    private UUID ownerId;
    private String value;
    private Integer type;
    private Integer status;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

}
