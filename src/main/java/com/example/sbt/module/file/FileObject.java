package com.example.sbt.module.file;

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
@Builder
@Entity
@Table(name = "file_object")
public class FileObject extends BaseEntity {

    @Column(name = "file_url", columnDefinition = "text")
    private UUID fileUrl;

}
