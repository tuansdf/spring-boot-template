package com.example.sbt.features.loginaudit.entity;

import com.example.sbt.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "login_audit",
        indexes = {
                @Index(name = "login_audit_comp_user_id_created_at_idx", columnList = "user_id, created_at"),
        }
)
public class LoginAudit extends BaseEntity {
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "is_success")
    private Boolean isSuccess;
}
