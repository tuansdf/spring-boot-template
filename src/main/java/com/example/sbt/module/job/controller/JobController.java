package com.example.sbt.module.job.controller;

import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.module.file.service.FileObjectService;
import com.example.sbt.module.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Secured(PermissionCode.JOB_EXECUTION)
@RestController
@RequestMapping("/v1/jobs")
public class JobController {

    private final TokenService tokenService;
    private final FileObjectService fileObjectService;

    @GetMapping("/tokens/delete-expired")
    public ResponseEntity<CommonResponse<Object>> deleteExpiredTokens() {
        tokenService.deleteExpiredTokensAsync();
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @GetMapping("/files/delete-pending")
    public ResponseEntity<CommonResponse<Object>> deletePendingFiles() {
        fileObjectService.deleteExpiredPendingUploadAsync();
        return ResponseEntity.ok(new CommonResponse<>());
    }

}
