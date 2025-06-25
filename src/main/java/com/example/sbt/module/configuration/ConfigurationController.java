package com.example.sbt.module.configuration;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.configuration.dto.SearchConfigurationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/configurations")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping("/values")
    public ResponseEntity<CommonResponse<Map<String, String>>> findValues(@RequestParam Set<String> keys) {
        var result = configurationService.findPublicValues(keys);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/{code}")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<ConfigurationDTO>> findOneByCode(@PathVariable String code) {
        var result = configurationService.findOneByCodeOrThrow(code);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PutMapping
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<ConfigurationDTO>> save(@RequestBody ConfigurationDTO requestDTO) {
        var result = configurationService.save(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/search")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<PaginationData<ConfigurationDTO>>> search(
            @RequestParam(required = false) Long pageNumber,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant createdAtFrom,
            @RequestParam(required = false) Instant createdAtTo,
            @RequestParam(required = false, defaultValue = "false") Boolean count) {
        var requestDTO = SearchConfigurationRequestDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .code(code)
                .status(status)
                .createdAtTo(createdAtTo)
                .createdAtFrom(createdAtFrom)
                .build();
        var result = configurationService.search(requestDTO, count);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

}
