package com.example.sbt.module.remoteconfig;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.remoteconfig.dto.RemoteConfigDTO;
import com.example.sbt.module.remoteconfig.dto.SearchRemoteConfigRequestDTO;
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
@RequestMapping("/v1/remote-configs")
public class RemoteConfigController {

    private final RemoteConfigService remoteConfigService;

    @GetMapping("/code/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RemoteConfigDTO>> findOneByCode(@PathVariable String code) {
        var result = remoteConfigService.findOneByCodeOrThrow(code);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{id}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RemoteConfigDTO>> findOneById(@PathVariable UUID id) {
        var result = remoteConfigService.findOneByIdOrThrow(id);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<RemoteConfigDTO>> save(@RequestBody RemoteConfigDTO requestDTO) {
        var result = remoteConfigService.save(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationData<RemoteConfigDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchRemoteConfigRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .code(code)
                .status(status)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = remoteConfigService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

}
