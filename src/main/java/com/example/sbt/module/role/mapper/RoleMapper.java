package com.example.sbt.module.role.mapper;

import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.module.permission.PermissionService;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleMapper {

    private final CommonMapper commonMapper;
    private final PermissionService permissionService;

    public RoleDTO toDTO(Role role) {
        RoleDTO result = commonMapper.toDTO(role);
        result.setPermissionIds(permissionService.findAllIdsByRoleId(result.getId()));
        result.setPermissionCodes(permissionService.findAllCodesByRoleId(result.getId()));
        return result;
    }

}
