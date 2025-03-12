package com.example.demo.module.configuration.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ConfigurationDTO {

    private UUID id;
    private String code;
    private String value;
    private String description;
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

}
