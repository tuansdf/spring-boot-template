package org.tuanna.xcloneserver.entities;

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
public class Token extends BaseEntity {

    @Column(name = "owner_id", columnDefinition = "uuid")
    private UUID ownerId;
    @Column(name = "value", columnDefinition = "text")
    private String value;
    @Column(name = "type", columnDefinition = "text")
    private String type;
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
    @Column(name = "status", columnDefinition = "text")
    private String status;
    @Column(name = "created_by", columnDefinition = "uuid", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

}
