package com.example.sbt.module.file;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.file.dto.SearchFileRequestDTO;
import com.example.sbt.module.file.service.FileObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/files")
public class FileObjectController {

    private final FileObjectService fileObjectService;

    @PostMapping
    public ResponseEntity<CommonResponse<Object>> uploadImage(@RequestParam MultipartFile file) {
        fileObjectService.uploadFile(file, null);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<FileObjectDTO>> findOne(@PathVariable UUID id) {
        var result = fileObjectService.getFileById(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/temporary")
    public ResponseEntity<CommonResponse<FileObjectPendingDTO>> createPendingFileUpload(@RequestBody TemporaryUploadRequest request) {
        var result = fileObjectService.createPendingFileUpload(request.mimeType());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/temporary/{id}")
    public ResponseEntity<CommonResponse<FileObjectDTO>> savePendingFileUpload(@PathVariable UUID id) {
        var result = fileObjectService.savePendingFileUpload(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PaginationData<FileObjectDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) Long fileSizeFrom,
            @RequestParam(required = false) Long fileSizeTo,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchFileRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .fileSizeFrom(fileSizeFrom)
                .fileSizeTo(fileSizeTo)
                .createdAtFrom(createdAtFrom)
                .createdAtTo(createdAtTo)
                .fileType(fileType)
                .orderBy(orderBy)
                .orderDirection(orderDirection)
                .createdBy(RequestContext.get().getUserId())
                .build();
        var result = fileObjectService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    public record TemporaryUploadRequest(String mimeType) {
    }

}
