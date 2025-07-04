package com.example.sbt.module.email.controller;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsDTO;
import com.example.sbt.module.email.dto.SearchEmailRequestDTO;
import com.example.sbt.module.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/emails")
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PaginationData<EmailDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchEmailRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .userId(RequestContext.get().getUserId())
                .status(status)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = emailService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<EmailDTO>> findOneById(@PathVariable UUID id) {
        var result = emailService.findOneById(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/stats")
    public ResponseEntity<CommonResponse<EmailStatsDTO>> getStats() {
        var result = emailService.getStatsByUser(RequestContext.get().getUserId());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }
}
