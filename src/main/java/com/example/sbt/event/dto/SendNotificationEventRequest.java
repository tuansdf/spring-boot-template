package com.example.sbt.event.dto;

import com.example.sbt.core.dto.RequestContextData;
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

    private RequestContextData requestContextData;
    private NotificationDTO notification;

}
