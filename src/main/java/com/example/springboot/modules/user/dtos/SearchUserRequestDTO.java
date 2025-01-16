package com.example.springboot.modules.user.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchUserRequestDTO {

    private Integer pageNumber;
    private Integer pageSize;
    private String username;
    private String email;
    private String status;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

}
