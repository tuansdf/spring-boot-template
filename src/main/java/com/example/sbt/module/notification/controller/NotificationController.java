package com.example.sbt.module.notification.controller;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContextHolder;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.notification.dto.NotificationStatsDTO;
import com.example.sbt.module.notification.dto.SearchNotificationRequestDTO;
import com.example.sbt.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PaginationData<NotificationDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count
    ) {
        var requestDTO = SearchNotificationRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .userId(RequestContextHolder.get().getUserId())
                .status(status)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = notificationService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<NotificationDTO>> findOneById(
            @PathVariable UUID id
    ) {
        var result = notificationService.findOneById(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/stats")
    public ResponseEntity<CommonResponse<NotificationStatsDTO>> getStats() {
        var result = notificationService.getStatsByUser(RequestContextHolder.get().getUserId());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }
}
