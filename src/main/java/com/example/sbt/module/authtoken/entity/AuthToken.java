package com.example.sbt.module.authtoken.entity;

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
        name = "auth_token",
        indexes = {
                @Index(name = "auth_token_user_id_idx", columnList = "user_id"),
                @Index(name = "auth_token_valid_from_idx", columnList = "valid_from"),
                @Index(name = "auth_token_created_at_idx", columnList = "created_at"),
        }
)
public class AuthToken extends BaseEntity {
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "valid_from")
    private Instant validFrom;
    @Column(name = "type", length = 32)
    private String type;
}
