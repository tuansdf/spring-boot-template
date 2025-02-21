package com.example.demo.modules.user.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchUserRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String username;
    private String email;
    private String status;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

}
