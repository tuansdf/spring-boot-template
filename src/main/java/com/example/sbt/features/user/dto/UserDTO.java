package com.example.sbt.features.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String name;
    @JsonIgnore
    private String password;
    private Boolean isEnabled;
    private Boolean isVerified;
    private Boolean isOtpEnabled;
    private String otpSecret;
    private Instant createdAt;
    private Instant updatedAt;

    private List<UUID> roleIds;
    private List<String> roleCodes;
    private List<String> permissionCodes;
}
