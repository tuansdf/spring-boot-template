package org.tuanna.xcloneserver.modules.permission;

import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    PermissionDTO save(PermissionDTO permissionDTO);

    PermissionDTO findOneById(Long id);

    List<String> findAllCodesByRoleId(Long roleId);

    List<String> findAllCodesByUserId(UUID userId);

    List<PermissionDTO> findAllByRoleId(Long roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

}
