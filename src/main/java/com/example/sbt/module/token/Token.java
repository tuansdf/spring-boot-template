package com.example.sbt.module.token;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "token")
public class Token extends BaseEntity {

    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "type")
    private Integer type;
    @Column(name = "status")
    private String status;

}
