package com.example.sbt.module.scheduledjob.controller;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.module.file.service.FileObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Secured(PermissionCode.SCHEDULED_JOB)
@RestController
@RequestMapping("/v1/scheduled-jobs")
public class ScheduledJobController {
    private final FileObjectService fileObjectService;

    @GetMapping("/files/delete-pending")
    public ResponseEntity<CommonResponse<Object>> deletePendingFiles() {
        fileObjectService.deleteExpiredPendingUploadAsync();
        return ResponseEntity.ok(new CommonResponse<>());
    }
}
