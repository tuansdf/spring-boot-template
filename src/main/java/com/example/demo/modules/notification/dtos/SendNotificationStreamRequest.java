package com.example.demo.modules.notification.dtos;

import com.example.demo.dtos.RequestContext;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendNotificationStreamRequest implements Serializable {

    private RequestContext requestContext;
    private UUID notificationId;

}
