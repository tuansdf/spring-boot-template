package com.example.sbt.module.user.entity;

import com.example.sbt.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "user_role",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_role_rel_idx", columnNames = {"user_id", "role_id"}),
        },
        indexes = {
                @Index(name = "user_role_role_id_idx", columnList = "role_id"),
                @Index(name = "user_role_created_at_idx", columnList = "created_at"),
        }
)
public class UserRole extends BaseEntity {
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "role_id", updatable = false)
    private UUID roleId;
}
