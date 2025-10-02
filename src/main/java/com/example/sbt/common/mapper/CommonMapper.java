package com.example.sbt.common.mapper;

import com.example.sbt.features.authtoken.dto.AuthTokenDTO;
import com.example.sbt.features.authtoken.entity.AuthToken;
import com.example.sbt.features.configuration.dto.ConfigurationDTO;
import com.example.sbt.features.configuration.entity.Configuration;
import com.example.sbt.features.email.dto.EmailDTO;
import com.example.sbt.features.email.dto.SendEmailRequest;
import com.example.sbt.features.email.entity.Email;
import com.example.sbt.features.file.dto.FileObjectDTO;
import com.example.sbt.features.file.dto.FileObjectPendingDTO;
import com.example.sbt.features.file.entity.FileObject;
import com.example.sbt.features.file.entity.FileObjectPending;
import com.example.sbt.features.loginaudit.dto.LoginAuditDTO;
import com.example.sbt.features.loginaudit.entity.LoginAudit;
import com.example.sbt.features.notification.dto.NotificationDTO;
import com.example.sbt.features.notification.dto.SendNotificationRequest;
import com.example.sbt.features.notification.entity.Notification;
import com.example.sbt.features.permission.dto.PermissionDTO;
import com.example.sbt.features.permission.entity.Permission;
import com.example.sbt.features.role.dto.RoleDTO;
import com.example.sbt.features.role.entity.Role;
import com.example.sbt.features.userdevice.dto.UserDeviceDTO;
import com.example.sbt.features.userdevice.entity.UserDevice;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommonMapper {
    Permission toEntity(PermissionDTO v);

    PermissionDTO toDTO(Permission v);

    PermissionDTO clone(PermissionDTO v);

    Role toEntity(RoleDTO v);

    RoleDTO toDTO(Role v);

    RoleDTO clone(RoleDTO v);

    Configuration toEntity(ConfigurationDTO v);

    ConfigurationDTO toDTO(Configuration v);

    ConfigurationDTO clone(ConfigurationDTO v);

    AuthToken toEntity(AuthTokenDTO v);

    AuthTokenDTO toDTO(AuthToken v);

    AuthTokenDTO clone(AuthTokenDTO v);

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
