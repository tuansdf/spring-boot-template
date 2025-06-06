package com.example.sbt.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String username;
    private String email;
    private String status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
