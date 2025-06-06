package com.example.sbt.module.role.entity;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "role_id")
    private UUID roleId;
    @Column(name = "permission_id")
    private UUID permissionId;

}
