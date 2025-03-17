package com.example.demo.module.user.dto;

import com.example.demo.common.util.ConversionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

    private Set<UUID> roleIds;
    private Set<String> roleCodes;
    private Set<String> permissionCodes;

    // USER_SEARCH_CONTACT
    public UserDTO(UUID id, String username) {
        setId(id);
        setUsername(username);
    }

    // USER_SEARCH
    public UserDTO(UUID id, String username, String email, String name, Integer status,
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

    public void setRoleCodes(String roles) {
        if (StringUtils.isBlank(roles)) {
            this.roleCodes = new HashSet<>();
            return;
        }
        this.roleCodes = Arrays.stream(roles.split(",")).map(x -> ConversionUtils.safeToString(x).trim()).collect(Collectors.toSet());
    }

    public void setPermissionCodes(String permissions) {
        if (StringUtils.isBlank(permissions)) {
            this.permissionCodes = new HashSet<>();
            return;
        }
        this.permissionCodes = Arrays.stream(permissions.split(",")).map(x -> ConversionUtils.safeToString(x).trim()).collect(Collectors.toSet());
    }

}
