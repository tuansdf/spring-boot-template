package com.example.demo.module.permission;

import com.example.demo.common.dto.PaginationResponseData;
import com.example.demo.module.permission.dto.PermissionDTO;
import com.example.demo.module.permission.dto.SearchPermissionRequestDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {

    PermissionDTO save(PermissionDTO permissionDTO);

    void setRolePermissions(UUID roleId, Set<UUID> permissionIds);

    PermissionDTO findOneById(UUID id);

    PermissionDTO findOneByIdOrThrow(UUID id);

    PermissionDTO findOneByCode(String code);

    PermissionDTO findOneByCodeOrThrow(String code);

    Set<UUID> findAllIdsByRoleId(UUID roleId);

    Set<String> findAllCodesByRoleId(UUID roleId);

    Set<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(UUID roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

    PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCount);

}
