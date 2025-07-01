package com.example.sbt.module.file.entity;

import com.example.sbt.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "file_object_pending",
        indexes = {
                @Index(name = "file_object_pending_created_by_idx", columnList = "created_by"),
                @Index(name = "file_object_pending_expires_at_idx", columnList = "expires_at"),
                @Index(name = "file_object_pending_created_at_idx", columnList = "created_at"),
        }
)
public class FileObjectPending extends BaseEntity {

    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;
    @Column(name = "file_name", columnDefinition = "text")
    private String fileName;
    @Column(name = "file_type", columnDefinition = "text")
    private String fileType;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "created_by")
    private UUID createdBy;

}
