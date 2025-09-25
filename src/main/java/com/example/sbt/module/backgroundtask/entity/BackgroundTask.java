package com.example.sbt.module.backgroundtask.entity;

import com.example.sbt.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
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
        name = "background_task",
        indexes = {
                @Index(name = "background_task_created_by_idx", columnList = "created_by"),
                @Index(name = "background_task_file_id_idx", columnList = "file_id"),
                @Index(name = "background_task_cache_key_idx", columnList = "cache_key"),
                @Index(name = "background_task_created_at_idx", columnList = "created_at"),
        }
)
public class BackgroundTask extends BaseEntity {
    @Column(name = "file_id")
    private UUID fileId;
    @Column(name = "cache_key", length = 64)
    private String cacheKey;
    @Column(name = "type", length = 32)
    private String type;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16)
    private Status status;
    @Column(name = "error_message", length = 255)
    private String errorMessage;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    public enum Status {
        ENQUEUED,
        PROCESSING,
        SUCCEEDED,
        FAILED,
    }
}
