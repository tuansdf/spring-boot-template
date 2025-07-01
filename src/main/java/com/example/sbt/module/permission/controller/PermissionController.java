package com.example.sbt.module.permission.controller;

import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.permission.dto.PermissionDTO;
import com.example.sbt.module.permission.dto.SearchPermissionRequestDTO;
import com.example.sbt.module.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/code/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOneByCode(@PathVariable String code) {
        var result = permissionService.findOneByCodeOrThrow(code);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOne(@PathVariable UUID id) {
        var result = permissionService.findOneByIdOrThrow(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> save(@RequestBody PermissionDTO requestDTO) {
        var result = permissionService.save(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationData<PermissionDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchPermissionRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .code(code)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = permissionService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

}
