package com.example.demo.modules.email.dtos;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class EmailDTO {

    private UUID id;
    private String fromEmail;
    private String toEmail;
    private String ccEmail;
    private String subject;
    private String content;
    private Integer type;
    private Integer retryCount;
    private Integer status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
