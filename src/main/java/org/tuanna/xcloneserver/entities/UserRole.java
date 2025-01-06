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
@Table(name = "user_role")
public class UserRole extends BaseEntity {

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;
    @Column(name = "role_id", columnDefinition = "bigint")
    private Long roleId;

}
