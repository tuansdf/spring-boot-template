package com.example.sbt.module.user.entity;

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
        name = "user_role",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_role_rel_idx", columnNames = {"user_id", "role_id"}),
        },
        indexes = {
                @Index(name = "user_role_created_at_idx", columnList = "created_at"),
        }
)
public class UserRole extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "role_id")
    private UUID roleId;

}
