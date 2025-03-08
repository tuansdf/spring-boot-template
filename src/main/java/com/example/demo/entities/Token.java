package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "token")
public class Token extends AbstractEntity {

    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
    @Column(name = "type")
    private Integer type;
    @Column(name = "status")
    private Integer status;

}
