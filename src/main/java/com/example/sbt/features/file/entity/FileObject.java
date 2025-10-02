package com.example.sbt.features.file.entity;

import com.example.sbt.infrastructure.persistence.BaseEntity;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "file_object",
        indexes = {
                @Index(name = "file_object_created_by_idx", columnList = "created_by"),
                @Index(name = "file_object_created_at_idx", columnList = "created_at"),
        }
)
public class FileObject extends BaseEntity {
    @Column(name = "file_path", length = 255)
    private String filePath;
    @Column(name = "preview_file_path", length = 255)
    private String previewFilePath;
    @Column(name = "filename", length = 255)
    private String filename;
    @Column(name = "file_type", length = 255)
    private String fileType;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
}
