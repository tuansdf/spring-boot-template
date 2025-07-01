package com.example.sbt.module.userdevice.controller;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.module.userdevice.dto.UserDeviceDTO;
import com.example.sbt.module.userdevice.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user-devices")
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @PutMapping
    public ResponseEntity<CommonResponse<UserDeviceDTO>> save(@RequestBody UserDeviceDTO requestDTO) {
        var result = userDeviceService.save(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

}
