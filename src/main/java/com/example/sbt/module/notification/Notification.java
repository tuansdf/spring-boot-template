package com.example.sbt.module.notification;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "notification")
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
