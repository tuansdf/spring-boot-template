package com.example.sbt.module.permission;

import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.module.permission.dto.PermissionDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "permission")
@SqlResultSetMapping(name = ResultSetName.PERMISSION_SEARCH, classes = {
        @ConstructorResult(targetClass = PermissionDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "status", type = Integer.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Permission extends BaseEntity {

    @Column(name = "code", unique = true, updatable = false)
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    private Integer status;

}
