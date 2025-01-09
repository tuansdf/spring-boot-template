package org.tuanna.xcloneserver.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tuanna.xcloneserver.entities.Configuration;
import org.tuanna.xcloneserver.entities.Permission;
import org.tuanna.xcloneserver.entities.Role;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.modules.configuration.dtos.ConfigurationDTO;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommonMapper {

    User toEntity(UserDTO v);

    UserDTO toDTO(User v);

    UserDTO clone(UserDTO v);

    Permission toEntity(PermissionDTO v);

    PermissionDTO toDTO(Permission v);

    PermissionDTO clone(PermissionDTO v);

    Role toEntity(RoleDTO v);

    RoleDTO toDTO(Role v);

    RoleDTO clone(RoleDTO v);

    Configuration toEntity(ConfigurationDTO v);

    ConfigurationDTO toDTO(Configuration v);

    ConfigurationDTO clone(ConfigurationDTO v);

}
