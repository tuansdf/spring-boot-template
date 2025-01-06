package org.tuanna.xcloneserver.modules.permission;

import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    List<PermissionDTO> findAllByRoleId(Long roleId);

    List<PermissionDTO> findAllByUserId(UUID userId);

}
