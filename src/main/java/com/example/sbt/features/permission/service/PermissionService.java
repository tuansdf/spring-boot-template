package com.example.sbt.features.permission.service;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.features.permission.dto.PermissionDTO;
import com.example.sbt.features.permission.dto.SearchPermissionRequest;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    PermissionDTO save(PermissionDTO permissionDTO);

    void setRolePermissions(UUID roleId, List<UUID> permissionIds);

    PermissionDTO findOneById(UUID id);

    PermissionDTO findOneByIdOrThrow(UUID id);

    PermissionDTO findOneByCode(String code);

    PermissionDTO findOneByCodeOrThrow(String code);

    List<UUID> findAllIdsByRoleId(UUID roleId);

    List<String> findAllCodesByRoleId(UUID roleId);

    List<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(UUID roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

    PaginationData<PermissionDTO> search(SearchPermissionRequest requestDTO, boolean isCount);
}
