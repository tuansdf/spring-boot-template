package com.example.sbt.module.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailStatsDTO {
    private Long totalUnread;
    private Long totalRead;
}
