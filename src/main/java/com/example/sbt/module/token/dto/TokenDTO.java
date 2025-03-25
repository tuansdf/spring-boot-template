package com.example.sbt.module.token.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDTO {

    private UUID id;
    private UUID ownerId;
    private Integer type;
    private Integer status;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonIgnore
    private String value;

}
