package com.example.demo.modules.user.dtos;

import lombok.*;

import java.time.Instant;

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
    private Integer status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
