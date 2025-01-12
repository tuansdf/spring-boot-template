package org.tuanna.xcloneserver.modules.notification;

import org.tuanna.xcloneserver.modules.notification.dtos.NotificationDTO;

public interface NotificationService {

    NotificationDTO send(NotificationDTO notificationDTO);

}
