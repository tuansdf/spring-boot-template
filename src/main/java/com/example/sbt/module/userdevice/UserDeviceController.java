package com.example.sbt.module.userdevice;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.util.ExceptionUtils;
import com.example.sbt.module.userdevice.dto.UserDeviceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user-devices")
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDeviceDTO>> findOne(@PathVariable UUID id) {
        try {
            var result = userDeviceService.findOneById(id);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDeviceDTO>> save(@RequestBody UserDeviceDTO requestDTO) {
        try {
            var result = userDeviceService.save(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
