package com.example.demo.module.user;

import com.example.demo.common.constant.PermissionCode;
import com.example.demo.common.dto.CommonResponse;
import com.example.demo.common.dto.PaginationResponseData;
import com.example.demo.common.util.ExceptionUtils;
import com.example.demo.module.user.dto.SearchUserRequestDTO;
import com.example.demo.module.user.dto.UserDTO;
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

    @PatchMapping
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
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
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
