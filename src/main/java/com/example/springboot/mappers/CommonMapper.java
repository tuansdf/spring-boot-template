package com.example.springboot.mappers;

import com.example.springboot.entities.*;
import com.example.springboot.modules.configuration.dtos.ConfigurationDTO;
import com.example.springboot.modules.email.dtos.EmailDTO;
import com.example.springboot.modules.notification.dtos.NotificationDTO;
import com.example.springboot.modules.permission.dtos.PermissionDTO;
import com.example.springboot.modules.role.dtos.RoleDTO;
import com.example.springboot.modules.token.dtos.TokenDTO;
import com.example.springboot.modules.user.dtos.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

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

    Token toEntity(TokenDTO v);

    TokenDTO toDTO(Token v);

    TokenDTO clone(TokenDTO v);

    Email toEntity(EmailDTO v);

    EmailDTO toDTO(Email v);

    EmailDTO clone(EmailDTO v);

    Notification toEntity(NotificationDTO v);

    NotificationDTO toDTO(Notification v);

    NotificationDTO clone(NotificationDTO v);

}
