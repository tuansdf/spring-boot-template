package com.example.sbt.module.backgroundtask.entity;

import com.example.sbt.core.entity.BaseEntity;
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
    @Column(name = "cache_key")
    private String cacheKey;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
}
