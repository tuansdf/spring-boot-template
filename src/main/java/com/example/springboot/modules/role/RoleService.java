package com.example.springboot.modules.role;

import com.example.springboot.dtos.PaginationResponseData;
import com.example.springboot.exception.CustomException;
import com.example.springboot.modules.role.dtos.RoleDTO;
import com.example.springboot.modules.role.dtos.SearchRoleRequestDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

    RoleDTO save(RoleDTO roleDTO);

    void addToUser(UUID userId, Set<Long> roleIds);

    RoleDTO findOneById(Long id);

    RoleDTO findOneByIdOrThrow(Long id);

    RoleDTO findOneByCode(String code);

    RoleDTO findOneByCodeOrThrow(String code);

    List<String> findAllCodesByUserId(UUID userId);

    List<RoleDTO> findAllByUserId(UUID userId);

    PaginationResponseData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCount);

}
