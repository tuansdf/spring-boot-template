package org.tuanna.xcloneserver.entities;

import jakarta.persistence.*;
import lombok.*;
import org.tuanna.xcloneserver.constants.ResultSetName;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;

import java.time.OffsetDateTime;
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
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "description", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created_by", type = UUID.class),
                @ColumnResult(name = "updated_by", type = UUID.class),
                @ColumnResult(name = "created_at", type = OffsetDateTime.class),
                @ColumnResult(name = "updated_at", type = OffsetDateTime.class),
        })
})
public class Role extends BaseResourceEntity {

    @Column(name = "code", columnDefinition = "text", unique = true, updatable = false)
    private String code;
    @Column(name = "name", columnDefinition = "text")
    private String name;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "status", columnDefinition = "text")
    private String status;
    @Column(name = "created_by", columnDefinition = "uuid", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

}
