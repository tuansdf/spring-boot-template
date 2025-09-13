package com.example.sbt.module.permission.entity;

import com.example.sbt.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "permission",
        uniqueConstraints = {
                @UniqueConstraint(name = "permission_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "permission_created_at_idx", columnList = "created_at"),
        }
)
public class Permission extends BaseEntity {
    @Column(name = "code", unique = true, updatable = false, length = 64)
    private String code;
    @Column(name = "name", length = 255)
    private String name;
}
