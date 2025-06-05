package com.example.sbt.module.userdevice;

import com.example.sbt.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "user_device",
        indexes = {
                @Index(name = "user_device_user_id_idx", columnList = "user_id"),
                @Index(name = "user_device_created_at_idx", columnList = "created_at"),
        }
)
public class UserDevice extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "fcm_token")
    private String fcmToken;

}
