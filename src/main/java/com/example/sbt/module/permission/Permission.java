package com.example.sbt.module.permission;

import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.module.permission.dto.PermissionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "permission_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "permission_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.PERMISSION_SEARCH, classes = {
        @ConstructorResult(targetClass = PermissionDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Permission extends BaseEntity {

    @Column(name = "code", updatable = false)
    private String code;
    @Column(name = "name")
    private String name;

}
