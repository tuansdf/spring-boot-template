package com.example.demo.modules.configuration.dtos;

import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchConfigurationRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Integer status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
