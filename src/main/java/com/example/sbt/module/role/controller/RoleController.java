package com.example.sbt.module.role.controller;

import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.dto.SearchRoleRequestDTO;
import com.example.sbt.module.role.service.RoleService;
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
@RequestMapping("/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/code/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RoleDTO>> findOneByCode(@PathVariable String code) {
        var result = roleService.findOneByCodeOrThrow(code);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RoleDTO>> findOneById(@PathVariable UUID id) {
        var result = roleService.findOneByIdOrThrow(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RoleDTO>> save(@RequestBody RoleDTO requestDTO) {
        var result = roleService.save(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationData<RoleDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchRoleRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .code(code)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = roleService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

}
