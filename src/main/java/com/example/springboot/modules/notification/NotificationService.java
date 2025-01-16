package com.example.springboot.modules.notification;

import com.example.springboot.modules.notification.dtos.NotificationDTO;

public interface NotificationService {

    NotificationDTO send(NotificationDTO notificationDTO);

}
