package com.example.sbt.event.dto;

import com.example.sbt.common.dto.RequestContext;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendNotificationEventRequest implements Serializable {

    private RequestContext requestContext;
    private UUID notificationId;

}
