package com.example.sbt.module.file;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/files")
public class FileObjectController {

    private final FileObjectService fileObjectService;

    @PostMapping
    @Secured(PermissionCode.SYSTEM_ADMIN)
    public ResponseEntity<CommonResponse<Object>> uploadImage(@RequestParam MultipartFile file) throws IOException {
        fileObjectService.uploadFile(file, null);
        return ResponseEntity.ok(new CommonResponse<>());
    }

}
