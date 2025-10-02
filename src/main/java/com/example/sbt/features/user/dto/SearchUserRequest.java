package com.example.sbt.features.user.dto;

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
public class SearchUserRequest {
    private Long pageNumber;
    private Long pageSize;
    private UUID id;
    private String username;
    private String email;
    private UUID idFrom;
    private String usernameFrom;
    private String emailFrom;
    private Boolean isEnabled;
    private Boolean isVerified;
    private Instant createdAtFrom;
    private Instant createdAtTo;
    private String orderBy;
    private String orderDirection;

    private String cacheType;
    private Instant cacheTime;
    private Boolean isDetail;
}
