package com.example.demo.module.configuration;

import com.example.demo.common.constant.ResultSetName;
import com.example.demo.common.entity.BaseEntity;
import com.example.demo.module.configuration.dto.ConfigurationDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "configuration")
@SqlResultSetMapping(name = ResultSetName.CONFIGURATION_SEARCH, classes = {
        @ConstructorResult(targetClass = ConfigurationDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "code", type = String.class),
                @ColumnResult(name = "value", type = String.class),
                @ColumnResult(name = "description", type = String.class),
                @ColumnResult(name = "status", type = Integer.class),
                @ColumnResult(name = "created_at", type = Instant.class),
                @ColumnResult(name = "updated_at", type = Instant.class),
        })
})
public class Configuration extends BaseEntity {

    @Column(name = "code", columnDefinition = "text", unique = true, updatable = false)
    private String code;
    @Column(name = "value", columnDefinition = "text")
    private String value;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "status")
    private Integer status;

}
