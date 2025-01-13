package org.tuanna.xcloneserver.modules.role;

import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;
import org.tuanna.xcloneserver.modules.role.dtos.SearchRoleRequestDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDTO save(RoleDTO permissionDTO, UUID actionBy) throws CustomException;

    RoleDTO findOneById(Long id);

    RoleDTO findOneByIdOrThrow(Long id) throws CustomException;

    RoleDTO findOneByCode(String code);

    RoleDTO findOneByCodeOrThrow(String code) throws CustomException;

    List<String> findAllCodesByUserId(UUID userId);

    List<RoleDTO> findAllByUserId(UUID userId);

    PaginationResponseData<RoleDTO> search(SearchRoleRequestDTO requestDTO, boolean isCountOnly);

}
