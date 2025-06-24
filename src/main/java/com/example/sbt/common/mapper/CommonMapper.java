package com.example.sbt.common.mapper;

import com.example.sbt.module.configuration.Configuration;
import com.example.sbt.module.configuration.dto.ConfigurationDTO;
import com.example.sbt.module.email.Email;
import com.example.sbt.module.email.dto.EmailDTO;
import com.example.sbt.module.email.dto.SendEmailRequest;
import com.example.sbt.module.file.entity.FileObject;
import com.example.sbt.module.file.dto.FileObjectDTO;
import com.example.sbt.module.file.entity.FileObjectPending;
import com.example.sbt.module.file.dto.FileObjectPendingDTO;
import com.example.sbt.module.loginaudit.LoginAudit;
import com.example.sbt.module.loginaudit.LoginAuditDTO;
import com.example.sbt.module.notification.Notification;
import com.example.sbt.module.notification.dto.NotificationDTO;
import com.example.sbt.module.notification.dto.SendNotificationRequest;
import com.example.sbt.module.permission.Permission;
import com.example.sbt.module.permission.dto.PermissionDTO;
import com.example.sbt.module.role.dto.RoleDTO;
import com.example.sbt.module.role.entity.Role;
import com.example.sbt.module.token.Token;
import com.example.sbt.module.token.dto.TokenDTO;
import com.example.sbt.module.user.dto.UserDTO;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.userdevice.UserDevice;
import com.example.sbt.module.userdevice.dto.UserDeviceDTO;
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

    SendNotificationRequest toSendRequest(NotificationDTO v);

    SendEmailRequest toSendRequest(EmailDTO v);

    UserDevice toEntity(UserDeviceDTO v);

    UserDeviceDTO toDTO(UserDevice v);

    UserDeviceDTO clone(UserDeviceDTO v);

    LoginAuditDTO toDTO(LoginAudit v);

    LoginAudit toEntity(LoginAuditDTO v);

    FileObjectDTO toDTO(FileObject v);

    FileObject toEntity(FileObjectDTO v);

    FileObjectPendingDTO toDTO(FileObjectPending v);

    FileObjectPending toEntity(FileObjectPendingDTO v);

}
