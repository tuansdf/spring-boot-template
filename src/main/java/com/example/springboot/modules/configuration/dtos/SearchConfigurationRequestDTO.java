package com.example.springboot.modules.configuration.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchConfigurationRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String code;
    private String status;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

}
