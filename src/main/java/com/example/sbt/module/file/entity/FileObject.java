package com.example.sbt.module.file.entity;

import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.core.constant.ResultSetName;
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
                @Index(name = "file_object_created_at_idx", columnList = "created_at"),
                @Index(name = "file_object_created_by_idx", columnList = "created_by"),
        }
)
@SqlResultSetMapping(name = ResultSetName.FILE_SEARCH, classes = {
        @ConstructorResult(targetClass = FileObjectDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "file_path", type = String.class),
                @ColumnResult(name = "preview_file_path", type = String.class),
                @ColumnResult(name = "file_name", type = String.class),
                @ColumnResult(name = "file_type", type = String.class),
                @ColumnResult(name = "file_size", type = Long.class),
                @ColumnResult(name = "created_by", type = UUID.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class FileObject extends BaseEntity {

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
    @Column(name = "created_by")
    private UUID createdBy;

}
