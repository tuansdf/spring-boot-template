package com.example.sbt.module.notification.event;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.notification.dto.NotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendNotificationEventRequest implements Serializable {
    private RequestContext requestContext;
    private NotificationDTO notification;
}
