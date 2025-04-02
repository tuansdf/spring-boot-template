package com.example.sbt.module.role.entity;

import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.module.role.dto.RoleDTO;
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
@Table(name = "role")
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

    @Column(name = "code", unique = true, updatable = false)
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "text")
    private String description;

}
