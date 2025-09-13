package com.example.sbt.module.role.entity;

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
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "role_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "role_created_at_idx", columnList = "created_at"),
        }
)
public class Role extends BaseEntity {
    @Column(name = "code", unique = true, updatable = false, length = 64)
    private String code;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "description", length = 255)
    private String description;
}
