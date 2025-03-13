package com.example.demo.module.user.dto;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.util.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        if (StringUtils.isNotBlank(roles)) {
            this.roleCodes = new HashSet<>();
            CollectionUtils.addAll(this.roleCodes, roles.split(","));
        }
    }

    public void setPermissionCodes(String permissions) {
        if (StringUtils.isNotBlank(permissions)) {
            this.permissionCodes = new HashSet<>();
            CollectionUtils.addAll(this.permissionCodes, permissions.split(","));
        }
    }

    public void validateUpdate() {
        ValidationUtils.notEmpty(this.username, "Username is required");
        ValidationUtils.maxLength(this.username, 255, "Username exceeds the maximum length of 255 characters");
        ValidationUtils.notEmpty(this.email, "Email is required");
        ValidationUtils.isEmail(this.email, "Email is invalid");
        ValidationUtils.maxLength(this.email, 255, "Email exceeds the maximum length of 255 characters");
        ValidationUtils.maxLength(this.name, 255, "Name exceeds the maximum length of 255 characters");
        ValidationUtils.isIn(this.status, List.of(CommonStatus.ACTIVE, CommonStatus.INACTIVE, CommonStatus.PENDING), "Status is invalid");
    }

}
