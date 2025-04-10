package com.example.sbt.module.loginaudit;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "login_audit",
        indexes = {
                @Index(name = "login_audit_user_id_idx", columnList = "user_id"),
                @Index(name = "login_audit_created_at_idx", columnList = "created_at"),
        }
)
public class LoginAudit extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "is_success")
    private Boolean isSuccess;

}
