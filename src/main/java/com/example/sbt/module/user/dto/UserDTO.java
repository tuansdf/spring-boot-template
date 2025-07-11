package com.example.sbt.module.user.dto;

import com.example.sbt.shared.util.ConversionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String name;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private Boolean isVerified;
    private Boolean isOtpEnabled;
    private String otpSecret;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    private List<UUID> roleIds;
    private List<String> roleCodes;
    private List<String> permissionCodes;

    // USER_SEARCH_CONTACT
    public UserDTO(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    // USER_SEARCH
    public UserDTO(UUID id, String username, String email, String name, Boolean isVerified, Boolean isOtpEnabled, String status,
                   Instant createdAt, Instant updatedAt, String roleCodes, String permissionCodes) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.isVerified = isVerified;
        this.isOtpEnabled = isOtpEnabled;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setRoleCodes(roleCodes);
        setPermissionCodes(permissionCodes);
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    public void setRoleCodes(String roles) {
        if (StringUtils.isBlank(roles)) return;
        this.roleCodes = Arrays.stream(roles.split(",")).map(ConversionUtils::safeTrim).collect(Collectors.toCollection(ArrayList::new));
    }

    public void setPermissionCodes(String permissions) {
        if (StringUtils.isBlank(permissions)) return;
        this.permissionCodes = Arrays.stream(permissions.split(",")).map(ConversionUtils::safeTrim).collect(Collectors.toCollection(ArrayList::new));
    }
}
