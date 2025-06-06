package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.ResultSetName;
import com.example.sbt.common.entity.BaseEntity;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
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
        name = "configuration",
        uniqueConstraints = {
                @UniqueConstraint(name = "configuration_code_idx", columnNames = "code"),
        },
        indexes = {
                @Index(name = "configuration_created_at_idx", columnList = "created_at"),
        }
)
@SqlResultSetMapping(name = ResultSetName.CONFIGURATION_SEARCH, classes = {
        @ConstructorResult(targetClass = ConfigurationDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "value", type = String.class),
                @ColumnResult(name = "description", type = String.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Configuration extends BaseEntity {

    @Column(name = "code", updatable = false)
    private String code;
    @Column(name = "value", columnDefinition = "text")
    private String value;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "status")
    private String status;

}
