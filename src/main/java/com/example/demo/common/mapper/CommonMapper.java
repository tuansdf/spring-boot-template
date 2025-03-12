package com.example.demo.common.mapper;

import com.example.demo.module.configuration.Configuration;
import com.example.demo.module.configuration.dto.ConfigurationDTO;
import com.example.demo.module.email.Email;
import com.example.demo.module.email.dto.EmailDTO;
import com.example.demo.module.notification.Notification;
import com.example.demo.module.notification.dto.NotificationDTO;
import com.example.demo.module.permission.Permission;
import com.example.demo.module.permission.dto.PermissionDTO;
import com.example.demo.module.role.Role;
import com.example.demo.module.role.dto.RoleDTO;
import com.example.demo.module.token.Token;
import com.example.demo.module.token.dto.TokenDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.dto.UserDTO;
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
