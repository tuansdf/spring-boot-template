package org.tuanna.xcloneserver.modules.configuration.dtos;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ConfigurationDTO {

    private Long id;
    private String code;
    private String value;
    private String description;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
