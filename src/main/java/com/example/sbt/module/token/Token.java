package com.example.sbt.module.token;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(
        name = "token",
        indexes = {
                @Index(name = "token_owner_id_idx", columnList = "owner_id"),
                @Index(name = "token_created_at_idx", columnList = "created_at"),
        }
)
public class Token extends BaseEntity {

    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;

}
