package com.example.sbt.module.role;

import com.example.sbt.common.dto.PaginationData;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.dto.SearchRoleRequestDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDTO save(RoleDTO roleDTO);

    void setUserRoles(UUID userId, List<UUID> roleIds);

    RoleDTO findOneById(UUID id);

    RoleDTO findOneByIdOrThrow(UUID id);

    RoleDTO findOneByCode(String code);

    RoleDTO findOneByCodeOrThrow(String code);

    List<String> findAllCodesByUserId(UUID userId);

    List<RoleDTO> findAllByUserId(UUID userId);

    PaginationData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCount);

}
