package com.example.demo.modules.token.dtos;

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
    private Integer status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
