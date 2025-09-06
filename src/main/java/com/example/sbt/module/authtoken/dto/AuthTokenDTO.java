package com.example.sbt.module.authtoken.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenDTO {
    private UUID id;
    private UUID userId;
    private String type;
    private Instant validFrom;
    private Instant createdAt;
    private Instant updatedAt;

    @JsonIgnore
    private String value;
}
