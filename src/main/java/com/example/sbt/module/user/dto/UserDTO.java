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
    private String otpSecret;
    private Boolean otpEnabled;
    private String status;
    private Boolean isVerified;
    private Instant createdAt;
    private Instant updatedAt;

    private List<UUID> roleIds;
    private List<String> roleCodes;
    private List<String> permissionCodes;

    // USER_SEARCH_CONTACT
    public UserDTO(UUID id, String username) {
        setId(id);
        setUsername(username);
    }

    // USER_SEARCH
    public UserDTO(UUID id, String username, String email, String name, String status,
                   Instant createdAt, Instant updatedAt, String roles, String permissions) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setName(name);
        setStatus(status);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setRoleCodes(roles);
        setPermissionCodes(permissions);
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    public void setRoleCodes(String roles) {
        if (StringUtils.isBlank(roles)) {
            this.roleCodes = new ArrayList<>();
        } else {
            this.roleCodes = Arrays.stream(roles.split(",")).map(ConversionUtils::safeTrim).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void setPermissionCodes(String permissions) {
        if (StringUtils.isBlank(permissions)) {
            this.permissionCodes = new ArrayList<>();
        } else {
            this.permissionCodes = Arrays.stream(permissions.split(",")).map(ConversionUtils::safeTrim).collect(Collectors.toCollection(ArrayList::new));
        }
    }

}
