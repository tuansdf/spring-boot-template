package com.example.sbt.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequestDTO {
    private Long pageNumber;
    private Long pageSize;
    private UUID id;
    private String username;
    private String email;
    private UUID idFrom;
    private String usernameFrom;
    private String emailFrom;
    private String status;
    private Instant createdAtFrom;
    private Instant createdAtTo;
    private String orderBy;
    private String orderDirection;
}
