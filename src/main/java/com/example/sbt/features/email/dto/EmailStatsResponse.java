package com.example.sbt.features.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailStatsResponse {
    private Long totalUnread;
    private Long totalRead;
}
