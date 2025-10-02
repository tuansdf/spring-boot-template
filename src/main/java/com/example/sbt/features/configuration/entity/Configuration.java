package com.example.sbt.features.configuration.entity;

import com.example.sbt.infrastructure.persistence.BaseEntity;
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
        name = "configuration",
        uniqueConstraints = {
                @UniqueConstraint(name = "configuration_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "configuration_created_at_idx", columnList = "created_at"),
        }
)
public class Configuration extends BaseEntity {
    @Column(name = "code", unique = true, updatable = false, length = 64)
    private String code;
    @Column(name = "value", length = 255)
    private String value;
    @Column(name = "description", length = 255)
    private String description;
    @Column(name = "is_enabled")
    private Boolean isEnabled;
    @Column(name = "is_public")
    private Boolean isPublic;
}
