package com.example.demo.module.user.mapper;

import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.module.role.RoleService;
import com.example.demo.module.user.dto.UserDTO;
import com.example.demo.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final CommonMapper commonMapper;
    private final RoleService roleService;

    public UserDTO toDTO(User user) {
        UserDTO result = commonMapper.toDTO(user);
        result.setRoleCodes(roleService.findAllCodesByUserId(result.getId()));
        result.setPermissionCodes(roleService.findAllCodesByUserId(result.getId()));
        return result;
    }

}
