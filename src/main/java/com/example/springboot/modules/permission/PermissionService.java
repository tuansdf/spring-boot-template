package com.example.springboot.modules.permission;

import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.permission.dtos.PermissionDTO;
import com.example.springboot.modules.permission.dtos.SearchPermissionRequestDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionDTO save(PermissionDTO permissionDTO) throws CustomException;

    PermissionDTO findOneById(Long id);

    PermissionDTO findOneByIdOrThrow(Long id) throws CustomException;

    PermissionDTO findOneByCode(String code);

    PermissionDTO findOneByCodeOrThrow(String code) throws CustomException;

    List<String> findAllCodesByRoleId(Long roleId);

    List<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(Long roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

    PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCountOnly);

}
