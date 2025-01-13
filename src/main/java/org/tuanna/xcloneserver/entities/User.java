package org.tuanna.xcloneserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.tuanna.xcloneserver.constants.ResultSetName;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
                @ColumnResult(name = "email", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "password", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created_by", type = UUID.class),
                @ColumnResult(name = "updated_by", type = UUID.class),
                @ColumnResult(name = "created_at", type = OffsetDateTime.class),
                @ColumnResult(name = "updated_at", type = OffsetDateTime.class),
        })
})
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH_CONTACT, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
        })
})
public class User extends BaseEntity {

    @Column(name = "username", columnDefinition = "text", unique = true)
    private String username;
    @Column(name = "email", columnDefinition = "text", unique = true)
    private String email;
    @Column(name = "password", columnDefinition = "text")
    private String password;
    @Column(name = "name", columnDefinition = "text")
    private String name;
    @Column(name = "status", columnDefinition = "text")
    private String status;
    @Column(name = "created_by", columnDefinition = "uuid", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

}
