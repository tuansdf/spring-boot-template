package com.example.sbt.module.file;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "file_object",
        indexes = {
                @Index(name = "file_object_created_at_idx", columnList = "created_at"),
                @Index(name = "file_object_created_by_idx", columnList = "created_by"),
        }
)
public class FileObject extends BaseEntity {

    @Column(name = "created_by")
    private UUID createdBy;
    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;
    @Column(name = "preview_file_path", columnDefinition = "text")
    private String previewFilePath;
    @Column(name = "file_name", columnDefinition = "text")
    private String fileName;
    @Column(name = "file_type", columnDefinition = "text")
    private String fileType;
    @Column(name = "file_size")
    private Long fileSize;

}
