package org.tuanna.xcloneserver.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.tuanna.xcloneserver.entities.Permission;
import org.tuanna.xcloneserver.entities.Role;
import org.tuanna.xcloneserver.entities.User;
import org.tuanna.xcloneserver.modules.permission.dtos.PermissionDTO;
import org.tuanna.xcloneserver.modules.role.dtos.RoleDTO;
import org.tuanna.xcloneserver.modules.user.dtos.UserDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommonMapper {

    User toEntity(UserDTO v);

    UserDTO toDTO(User v);

    Permission toEntity(PermissionDTO v);

    PermissionDTO toDTO(Permission v);

    Role toEntity(RoleDTO v);

    RoleDTO toDTO(Role v);

}
