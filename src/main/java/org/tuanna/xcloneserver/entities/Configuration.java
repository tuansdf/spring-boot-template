package org.tuanna.xcloneserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "configuration")
public class Configuration extends BaseResourceEntity {

    @Column(name = "code", columnDefinition = "text")
    private String code;
    @Column(name = "value", columnDefinition = "text")
    private String value;
    @Column(name = "description", columnDefinition = "text")
    private String description;

}
