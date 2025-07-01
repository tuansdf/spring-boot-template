package com.example.sbt.module.notification.entity;

import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.entity.BaseEntity;
import com.example.sbt.module.notification.dto.NotificationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "notification_user_id_idx", columnList = "user_id"),
                @Index(name = "notification_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.NOTIFICATION_SEARCH, classes = {
        @ConstructorResult(targetClass = NotificationDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "user_id", type = UUID.class),
                @ColumnResult(name = "title", type = String.class),
                @ColumnResult(name = "body", type = String.class),
                @ColumnResult(name = "data", type = String.class),
                @ColumnResult(name = "topic", type = String.class),
                @ColumnResult(name = "retry_count", type = Integer.class),
                @ColumnResult(name = "type", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "send_status", type = String.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Notification extends BaseEntity {

    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "body", columnDefinition = "text")
    private String body;
    @Column(name = "data", columnDefinition = "text")
    private String data;
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "topic")
    private String topic;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;
    @Column(name = "send_status")
    private String sendStatus;

}
