package com.example.sbt.event.dto;

import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.module.notification.dto.NotificationDTO;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendNotificationEventRequest implements Serializable {

    private RequestContext requestContext;
    private NotificationDTO notification;

}
