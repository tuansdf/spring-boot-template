package org.tuanna.xcloneserver.modules.permission;

import org.tuanna.xcloneserver.dtos.PaginationResponseData;
import org.tuanna.xcloneserver.exception.CustomException;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.permission.dtos.SearchPermissionRequestDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionDTO save(PermissionDTO permissionDTO, UUID byUserId) throws CustomException;

    PermissionDTO findOneById(Long id);

    List<String> findAllCodesByRoleId(Long roleId);

    List<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(Long roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

    PaginationResponseData<PermissionDTO> search(SearchPermissionRequestDTO requestDTO, boolean isCountOnly);
}
