package com.example.sbt.module.file.entity;

import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.core.entity.BaseEntity;
import com.example.sbt.module.file.dto.FileObjectDTO;
import jakarta.persistence.*;
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
        name = "file_object",
        indexes = {
                @Index(name = "file_object_cache_key_idx", columnList = "cache_key"),
                @Index(name = "file_object_created_by_idx", columnList = "created_by"),
                @Index(name = "file_object_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.FILE_SEARCH, classes = {
        @ConstructorResult(targetClass = FileObjectDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "file_path", type = String.class),
                @ColumnResult(name = "preview_file_path", type = String.class),
                @ColumnResult(name = "filename", type = String.class),
                @ColumnResult(name = "file_type", type = String.class),
                @ColumnResult(name = "file_size", type = Long.class),
                @ColumnResult(name = "created_by", type = UUID.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
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
    @Column(name = "cache_key", length = 64)
    private String cacheKey;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
}
