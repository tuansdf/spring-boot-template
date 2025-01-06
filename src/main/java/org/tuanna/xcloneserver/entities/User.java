package org.tuanna.xcloneserver.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User extends BaseEntity {

    @Column(name = "username", columnDefinition = "text", unique = true)
    private String username;
    @Column(name = "email", columnDefinition = "text", unique = true)
    private String email;
    @Column(name = "password", columnDefinition = "text")
    private String password;
    @Column(name = "name", columnDefinition = "text")
    private String name;
    @Column(name = "status", columnDefinition = "text")
    private String status;
    @Column(name = "created_by", columnDefinition = "uuid", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

}
