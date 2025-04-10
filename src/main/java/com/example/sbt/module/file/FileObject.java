package com.example.sbt.module.file;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

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
        }
)
public class FileObject extends BaseEntity {

    @Column(name = "file_url", columnDefinition = "text")
    private String fileUrl;
    @Column(name = "preview_file_url", columnDefinition = "text")
    private String previewFileUrl;

}
