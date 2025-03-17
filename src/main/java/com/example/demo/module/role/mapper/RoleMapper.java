package com.example.demo.module.role.mapper;

import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.module.permission.PermissionService;
import com.example.demo.module.role.dto.RoleDTO;
import com.example.demo.module.role.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleMapper {

    private final CommonMapper commonMapper;
    private final PermissionService permissionService;

    public RoleDTO toDTO(Role role) {
        RoleDTO result = commonMapper.toDTO(role);
        result.setPermissionCodes(permissionService.findAllCodesByRoleId(result.getId()));
        return result;
    }

}
