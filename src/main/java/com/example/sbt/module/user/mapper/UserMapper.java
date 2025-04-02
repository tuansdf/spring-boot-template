package com.example.sbt.module.user.mapper;

import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.module.permission.PermissionService;
import com.example.sbt.module.role.RoleService;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final CommonMapper commonMapper;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public UserDTO toDTO(User user) {
        UserDTO result = commonMapper.toDTO(user);
        result.setRoleCodes(roleService.findAllCodesByUserId(result.getId()));
        result.setPermissionCodes(permissionService.findAllCodesByUserId(result.getId()));
        return result;
    }

}
