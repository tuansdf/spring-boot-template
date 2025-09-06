package com.example.sbt.module.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationStatsResponse {
    private Long totalUnread;
    private Long totalRead;
}
