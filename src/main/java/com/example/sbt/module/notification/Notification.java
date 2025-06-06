package com.example.sbt.module.notification;

import com.example.sbt.common.entity.BaseEntity;
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

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "title", columnDefinition = "text")
    private String title;
    @Column(name = "content", columnDefinition = "text")
    private String content;
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

}
