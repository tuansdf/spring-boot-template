package com.example.springboot.controllers;

import com.example.springboot.configs.RequestContextHolder;
import com.example.springboot.constants.PermissionCode;
import com.example.springboot.dtos.CommonResponse;
import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.modules.user.UserService;
import com.example.springboot.modules.user.dtos.ChangePasswordRequestDTO;
import com.example.springboot.modules.user.dtos.SearchUserRequestDTO;
import com.example.springboot.modules.user.dtos.UserDTO;
import com.example.springboot.utils.ConversionUtils;
import com.example.springboot.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> findOne(@PathVariable UUID id) {
        try {
            var result = userService.findOneById(id);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PatchMapping("/password")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> changePassword(@RequestBody ChangePasswordRequestDTO requestDTO) {
        try {
            UUID userId = ConversionUtils.toUUID(RequestContextHolder.get().getUserId());
            var result = userService.changePassword(requestDTO, userId);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PatchMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> updateProfile(@RequestBody UserDTO requestDTO) {
        try {
            var result = userService.updateProfile(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationResponseData<UserDTO>>> search(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) OffsetDateTime createdAtFrom,
            @RequestParam(required = false) OffsetDateTime createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        try {
            var requestDTO = SearchUserRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .username(username)
                    .email(email)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            var result = userService.search(requestDTO, count);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
