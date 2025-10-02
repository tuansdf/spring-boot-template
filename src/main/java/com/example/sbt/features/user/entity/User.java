package com.example.sbt.features.user.entity;

import com.example.sbt.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_username_idx", columnNames = "username"),
                @UniqueConstraint(name = "user_email_idx", columnNames = "email"),
        },
        indexes = {
                @Index(name = "user_created_at_idx", columnList = "created_at"),
        }
)
public class User extends BaseEntity {
    @Column(name = "username", unique = true, length = 64)
    private String username;
    @Column(name = "email", unique = true, length = 255)
    private String email;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "password", length = 255)
    private String password;
    @Column(name = "is_verified")
    private Boolean isVerified;
    @Column(name = "is_enabled")
    private Boolean isEnabled;
    @Column(name = "is_otp_enabled")
    private Boolean isOtpEnabled;
    @Column(name = "otp_secret", length = 255)
    private String otpSecret;
}
