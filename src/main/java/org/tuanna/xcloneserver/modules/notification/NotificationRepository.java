package org.tuanna.xcloneserver.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tuanna.xcloneserver.entities.Notification;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
