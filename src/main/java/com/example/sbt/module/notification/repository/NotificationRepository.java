package com.example.sbt.module.notification.repository;

import com.example.sbt.module.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Optional<Notification> findTopByIdAndUserId(UUID id, UUID userId);
}
