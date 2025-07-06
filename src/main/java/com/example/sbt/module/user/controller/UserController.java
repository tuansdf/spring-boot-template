package com.example.sbt.module.user.controller;

import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.user.dto.SearchUserRequestDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final LocaleHelper localeHelper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserDTO>> findCurrentUser() {
        UUID userId = RequestContext.get().getUserId();
        var result = userService.findOneById(userId);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> findOne(
            @PathVariable UUID id
    ) {
        var result = userService.findOneByIdOrThrow(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<UserDTO>> updateProfile(
            @RequestBody UserDTO requestDTO
    ) {
        requestDTO.setId(RequestContext.get().getUserId());
        var result = userService.updateProfile(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PatchMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> updateProfileById(
            @PathVariable UUID id,
            @RequestBody UserDTO requestDTO
    ) {
        requestDTO.setId(id);
        var result = userService.updateProfile(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationData<UserDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            @RequestParam(required = false, defaultValue = "false") Boolean count
    ) {
        var requestDTO = SearchUserRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .username(username)
                .email(email)
                .status(status)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .build();
        var result = userService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/export")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<FileObjectDTO>> export(
            @RequestParam(required = false) String status
    ) {
        var requestDTO = SearchUserRequestDTO.builder()
                .status(status)
                .build();
        userService.triggerExport(requestDTO);
        var message = localeHelper.getMessage("user.task.export.enqueued");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }
}
