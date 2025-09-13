package com.example.sbt.module.notification.entity;

import com.example.sbt.infrastructure.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class Notification extends BaseEntity {
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "body", columnDefinition = "text")
    private String body;
    @Column(name = "data", columnDefinition = "text")
    private String data;
    @Column(name = "topic", length = 255)
    private String topic;
    @Column(name = "retry_count")
    private Integer retryCount;
    @Column(name = "type", length = 32)
    private String type;
    @Column(name = "status", length = 16)
    private String status;
    @Column(name = "send_status", length = 16)
    private String sendStatus;
}
