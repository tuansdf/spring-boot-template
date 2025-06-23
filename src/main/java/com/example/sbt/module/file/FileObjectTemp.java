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
        name = "file_object_temp",
        indexes = {
                @Index(name = "file_object_temp_created_at_idx", columnList = "created_at"),
                @Index(name = "file_object_temp_created_by_idx", columnList = "created_by"),
        }
)
public class FileObjectTemp extends BaseEntity {

    @Column(name = "file_path", columnDefinition = "text")
    private String filePath;
    @Column(name = "upload_file_url", columnDefinition = "text")
    private String uploadFileUrl;
    @Column(name = "file_name", columnDefinition = "text")
    private String fileName;
    @Column(name = "file_type", columnDefinition = "text")
    private String fileType;
    @Column(name = "created_by")
    private UUID createdBy;

}
