package org.tuanna.xcloneserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "configuration")
public class Configuration extends BaseResourceEntity {

    @Column(name = "code", columnDefinition = "text", unique = true, updatable = false)
    private String code;
    @Column(name = "value", columnDefinition = "text")
    private String value;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "status", columnDefinition = "text")
    private String status;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

}
