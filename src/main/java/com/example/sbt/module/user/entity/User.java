package com.example.sbt.module.user.entity;

import com.example.sbt.core.entity.BaseEntity;
import com.example.sbt.core.constant.ResultSetName;
import com.example.sbt.module.user.dto.UserDTO;
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
        name = "_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_username_idx", columnNames = "username"),
                @UniqueConstraint(name = "user_email_idx", columnNames = "email"),
        },
        indexes = {
                @Index(name = "user_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
                @ColumnResult(name = "email", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
                @ColumnResult(name = "roles", type = String.class),
                @ColumnResult(name = "permissions", type = String.class),
        })
})
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH_CONTACT, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
        })
})
public class User extends BaseEntity {

    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "otp_secret")
    private String otpSecret;
    @Column(name = "otp_enabled")
    private Boolean otpEnabled;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    private String status;
    @Column(name = "is_verified")
    private Boolean isVerified;

}
