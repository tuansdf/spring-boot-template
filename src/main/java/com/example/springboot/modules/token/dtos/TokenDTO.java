package com.example.springboot.modules.token.dtos;

import lombok.*;

import java.time.OffsetDateTime;
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
    private String type;
    private OffsetDateTime expiresAt;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
