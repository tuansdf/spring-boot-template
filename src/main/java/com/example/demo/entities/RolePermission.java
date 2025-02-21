package com.example.demo.entities;

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
@Table(name = "role_permission")
public class RolePermission extends BaseEntity {

    @Column(name = "role_id")
    private UUID roleId;
    @Column(name = "permission_id")
    private UUID permissionId;

}
