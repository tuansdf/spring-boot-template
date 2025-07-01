package com.example.sbt.module.role.entity;

import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.module.role.dto.RoleDTO;
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
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "role_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "role_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.ROLE_SEARCH, classes = {
        @ConstructorResult(targetClass = RoleDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "description", type = String.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Role extends BaseEntity {

    @Column(name = "code", updatable = false)
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "text")
    private String description;

}
