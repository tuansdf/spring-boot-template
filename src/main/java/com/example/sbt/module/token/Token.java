package com.example.sbt.module.token;

import com.example.sbt.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "token",
        indexes = {
                @Index(name = "token_user_id_idx", columnList = "user_id"),
                @Index(name = "token_expires_at_idx", columnList = "expires_at"),
                @Index(name = "token_created_at_idx", columnList = "created_at"),
        }
)
public class Token extends BaseEntity {

    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;

}
