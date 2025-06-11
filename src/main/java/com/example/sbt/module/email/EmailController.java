package com.example.sbt.module.email;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.common.util.ExceptionUtils;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.EmailStatsDTO;
import com.example.sbt.module.email.dto.SearchEmailRequestDTO;
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
        try {
            var requestDTO = SearchEmailRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .userId(RequestHolder.getContext().getUserId())
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            var result = emailService.search(requestDTO, count);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<EmailDTO>> findOneById(@PathVariable UUID id) {
        try {
            var result = emailService.findOneById(id);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<CommonResponse<EmailStatsDTO>> getStats() {
        try {
            var result = emailService.getStatsByUser(RequestHolder.getContext().getUserId());
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
