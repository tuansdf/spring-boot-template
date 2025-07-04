package com.example.sbt.module.permission.service;

import com.example.sbt.core.dto.PaginationData;
import com.example.sbt.module.permission.dto.PermissionDTO;
import com.example.sbt.module.permission.dto.SearchPermissionRequestDTO;

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

    PaginationData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCount);
}
