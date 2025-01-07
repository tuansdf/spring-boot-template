package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.modules.permission.PermissionService;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.permission.dtos.SearchPermissionRequestDTO;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(permissionService.findOneById(id)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> save(@RequestBody PermissionDTO requestDTO) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(permissionService.save(requestDTO)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationResponseData<PermissionDTO>>> search(
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo) {
        try {
            SearchPermissionRequestDTO requestDTO = SearchPermissionRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .code(code)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            return ResponseEntity.ok(new CommonResponse<>(permissionService.search(requestDTO)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
