package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthenticationPrincipal;
import org.tuanna.xcloneserver.modules.user.UserService;
import org.tuanna.xcloneserver.modules.user.dtos.SearchUserRequestDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;
import org.tuanna.xcloneserver.utils.AuthUtils;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

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
            return ResponseEntity.ok(new CommonResponse<>(userService.findOneById(id)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> save(@RequestBody UserDTO requestDTO) {
        try {
            AuthenticationPrincipal principal = AuthUtils.getAuthenticationPrincipal();
            return ResponseEntity.ok(new CommonResponse<>(userService.save(requestDTO, principal.getUserId())));
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
            SearchUserRequestDTO requestDTO = SearchUserRequestDTO.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .username(username)
                    .email(email)
                    .status(status)
                    .createdAtTo(createdAtTo)
                    .createdAtFrom(createdAtFrom)
                    .build();
            return ResponseEntity.ok(new CommonResponse<>(userService.search(requestDTO, count)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
