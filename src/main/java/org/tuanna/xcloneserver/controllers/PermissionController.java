package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.modules.authentication.dtos.AuthenticationPrincipal;
import org.tuanna.xcloneserver.modules.permission.PermissionService;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.permission.dtos.SearchPermissionRequestDTO;
import org.tuanna.xcloneserver.utils.AuthUtils;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/code/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOneByCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(permissionService.findOneByCodeOrThrow(code)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> findOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(permissionService.findOneByIdOrThrow(id)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PermissionDTO>> save(@RequestBody PermissionDTO requestDTO) {
        try {
            AuthenticationPrincipal principal = AuthUtils.getAuthenticationPrincipal();
            return ResponseEntity.ok(new CommonResponse<>(permissionService.save(requestDTO, principal.getUserId())));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationResponseData<PermissionDTO>>> search(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        try {
            SearchPermissionRequestDTO requestDTO = SearchPermissionRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .code(code)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            return ResponseEntity.ok(new CommonResponse<>(permissionService.search(requestDTO, count)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
