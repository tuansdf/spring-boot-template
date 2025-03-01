package com.example.demo.modules.role.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchRoleRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Integer status;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

}
