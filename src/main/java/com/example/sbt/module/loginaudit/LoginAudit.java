package com.example.sbt.module.loginaudit;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "login_audit")
public class LoginAudit extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "is_success")
    private Boolean isSuccess;

}
