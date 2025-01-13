package org.tuanna.xcloneserver.modules.notification.dtos;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NotificationDTO {

    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private String data;
    private Integer retryCount;
    private String type;
    private String topic;
    private String status;
    private UUID createdBy;
    private UUID updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
