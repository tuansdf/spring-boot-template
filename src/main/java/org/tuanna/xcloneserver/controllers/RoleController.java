package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.modules.role.RoleService;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;
import org.tuanna.xcloneserver.modules.role.dtos.SearchRoleRequestDTO;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RoleDTO>> findOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(roleService.findOneById(id)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RoleDTO>> save(@RequestBody RoleDTO requestDTO) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(roleService.save(requestDTO)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationResponseData<RoleDTO>>> search(
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        try {
            SearchRoleRequestDTO requestDTO = SearchRoleRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .code(code)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            return ResponseEntity.ok(new CommonResponse<>(roleService.search(requestDTO, count)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
