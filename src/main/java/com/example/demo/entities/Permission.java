package com.example.demo.entities;

import com.example.demo.constants.ResultSetName;
import com.example.demo.modules.permission.dtos.PermissionDTO;
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

    @Column(name = "code", columnDefinition = "text", unique = true, updatable = false)
    private String code;
    @Column(name = "name", columnDefinition = "text")
    private String name;
    @Column(name = "status")
    private Integer status;

}
