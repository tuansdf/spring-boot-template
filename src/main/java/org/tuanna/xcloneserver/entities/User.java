package org.tuanna.xcloneserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "username")
    private String username;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        ZonedDateTime now = ZonedDateTime.now();
        if (id == null) {
            id = UUIDUtils.generateId();
        }
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        ZonedDateTime now = ZonedDateTime.now();
        updatedAt = now;
    }

}
