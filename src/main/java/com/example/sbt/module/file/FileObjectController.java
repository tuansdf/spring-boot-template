package com.example.sbt.module.file;

import com.example.sbt.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/files")
public class FileObjectController {

    private final FileObjectService fileObjectService;

    @PostMapping
    public ResponseEntity<CommonResponse<Object>> uploadImage(@RequestParam MultipartFile file) throws IOException {
        fileObjectService.uploadFile(file, null);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<FileObjectDTO>> findOne(@PathVariable UUID id) {
        var result = fileObjectService.getFileById(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/temp")
    public ResponseEntity<CommonResponse<FileObjectTempDTO>> createTempUpload(@RequestBody TemporaryUploadRequest request) {
        var result = fileObjectService.createTempUploadFile(request.mimeType());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/temp/{id}")
    public ResponseEntity<CommonResponse<FileObjectDTO>> saveTempUpload(@PathVariable UUID id) {
        var result = fileObjectService.saveTempUploadFile(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    public record TemporaryUploadRequest(String mimeType) {
    }

}
