package org.tuanna.xcloneserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseResourceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

}
