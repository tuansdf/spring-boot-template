package com.example.sbt.module.role.entity;

import com.example.sbt.common.entity.BaseEntity;
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
        name = "role_permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "role_permission_rel_idx", columnNames = {"role_id", "permission_id"}),
        },
        indexes = {
                @Index(name = "role_permission_created_at_idx", columnList = "created_at"),
        }
)
public class RolePermission extends BaseEntity {

    @Column(name = "role_id", updatable = false)
    private UUID roleId;
    @Column(name = "permission_id", updatable = false)
    private UUID permissionId;

}
