package com.example.sbt.features.user.controller;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.features.file.dto.FileObjectDTO;
import com.example.sbt.features.user.dto.SearchUserRequest;
import com.example.sbt.features.user.dto.UserDTO;
import com.example.sbt.features.user.service.UserService;
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
        UUID userId = RequestContextHolder.get().getUserId();
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
        requestDTO.setId(RequestContextHolder.get().getUserId());
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
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            @RequestParam(required = false, defaultValue = "false") Boolean count
    ) {
        var requestDTO = SearchUserRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .id(id)
                .username(username)
                .email(email)
                .isEnabled(isEnabled)
                .isVerified(isVerified)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .isDetail(false)
                .build();
        var result = userService.search(requestDTO, ConversionUtils.safeToBoolean(count));
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/export")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<FileObjectDTO>> export() {
        var requestDTO = SearchUserRequest.builder().isDetail(true).build();
        userService.triggerExport(requestDTO);
        var message = localeHelper.getMessage("user.task.export.enqueued");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }
}
