package com.example.sbt.module.userdevice;

import com.example.sbt.common.entity.BaseEntity;
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
@Table(name = "user_device")
public class UserDevice extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "fcm_token")
    private String fcmToken;
    @Column(name = "status")
    private Integer status;

}
