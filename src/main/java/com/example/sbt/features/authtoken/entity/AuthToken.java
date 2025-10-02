package com.example.sbt.features.authtoken.entity;

import com.example.sbt.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 32)
    private Type type;

    public enum Type {
        OAUTH2,
        ACCESS_TOKEN,
        REFRESH_TOKEN,
        RESET_PASSWORD,
        ACTIVATE_ACCOUNT,
    }
}
